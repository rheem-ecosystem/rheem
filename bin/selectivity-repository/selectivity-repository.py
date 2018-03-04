import sys, json
import numpy as np
from sklearn.linear_model import LinearRegression

import warnings
warnings.filterwarnings(action="ignore", module="scipy", message="^internal gelsd")


def read_and_process_json(file, print_repository):
    # Read file
    with open(file) as f:
        content = f.readlines()
        content = [x.strip() for x in content]

    all_measurements = {}

    # Parse one json per line
    for line in content:
        parsed = json.loads(line)
        for execGroup in parsed['execGroups']:
            if ('selectivityKey' in execGroup['ctx']):
                selectivityKey = execGroup['ctx']['selectivityKey']
                if ('my.' in selectivityKey):
                    key = selectivityKey.split('-')[0]
                    if key not in all_measurements:
                        all_measurements[key] = {}
                        all_measurements[key]['in'] = []
                        all_measurements[key]['out'] = []
                    all_measurements[key]['in'].append(execGroup['ctx']['inCards'])
                    all_measurements[key]['out'].append(execGroup['ctx']['outCards'])

    # process every operator key
    for key in all_measurements:

        create_this_key = False

        xdata = []
        ydata = []

        minimumg_selectivity = sys.float_info.max
        maximum_selectivity = 0.0
        max_card = 0

        # for every measurement of this operator
        for i in range(0, len(all_measurements[key]['in'])):
            in_lower_max = 0
            in_upper_sum = 0
            use_measurement = True

            # sum over the inputs
            for j in range(0, len(all_measurements[key]['in'][i])):
                if all_measurements[key]['in'][i][j]['confidence'] != 1:
                    use_measurement = False
                if all_measurements[key]['in'][i][j]['confidence'] == 1:
                    if all_measurements[key]['in'][i][j]['lowerBound'] < 0 or in_upper_sum + \
                            all_measurements[key]['in'][i][j]['upperBound'] < 0:
                        use_measurement = False
                    in_lower_max = max(in_lower_max, all_measurements[key]['in'][i][j]['lowerBound'])
                    in_upper_sum = max(in_upper_sum, all_measurements[key]['in'][i][j]['upperBound'])
            out_lower_sum = 0
            out_upper_sum = 0

            #sum over the outputs
            for j in range(0, len(all_measurements[key]['out'][i])):
                if all_measurements[key]['out'][i][j]['confidence'] != 1:
                    use_measurement = False
                if all_measurements[key]['out'][i][j]['confidence'] == 1:
                    if all_measurements[key]['out'][i][j]['lowerBound'] < 0 or in_upper_sum + \
                            all_measurements[key]['out'][i][j]['upperBound'] < 0:
                        use_measurement = False
                    out_lower_sum = out_lower_sum + all_measurements[key]['out'][i][j]['lowerBound']
                    out_upper_sum = out_upper_sum + all_measurements[key]['out'][i][j]['upperBound']

            # calculate selectivity
            if use_measurement:
                selectivity = 1.0 * ((out_lower_sum * out_upper_sum)**(1/2)) / ((in_lower_max * in_upper_sum)**(1/2)  )

                # store min an max selectivity for minmax algorithm
                if selectivity < minimumg_selectivity:
                    create_this_key = True
                    minimumg_selectivity = selectivity
                if selectivity > maximum_selectivity:
                    maximum_selectivity = selectivity

                # calculate geometric mean selectivity for regression learning
                xdata.append((in_lower_max * in_upper_sum)**(1/2)) # TODO JRK: geometric mean right choice? better distinct lower and upper?
                if (in_lower_max * in_upper_sum)**(1/2) > max_card:
                    max_card = (in_lower_max * in_upper_sum)**(1/2)
                    all_measurements[key]['max_card'] = max_card
                ydata.append(selectivity)

        all_measurements[key]['xdata'] = xdata
        all_measurements[key]['ydata'] = ydata
        all_measurements[key]['min'] = minimumg_selectivity
        all_measurements[key]['max'] = maximum_selectivity

        if len(xdata) > 0 and len(ydata) > 0:

            # linear regression
            xdata = np.array(xdata)
            ydata = np.array(ydata)

            try:
                predictor = LinearRegression(n_jobs=-1)
                predictor.fit(X=[[x] for x in xdata], y=ydata)

                coefficient = predictor.coef_
                intercept = predictor.intercept_
            except ValueError:
                coefficient = [0.0]
                intercept = [0.0]

            all_measurements[key]['coefficient'] = coefficient
            all_measurements[key]['intercept'] = intercept



            # log curve fit
            params, residuals, rank, singular_values, rcond = np.polyfit(np.log([x if x!=1.0 else 1.0001 for x in xdata]), ydata, 1, full=True)

            all_measurements[key]['log_coeff'] = params[0]
            all_measurements[key]['log_intercept'] = params[1]

            def calculate_error_sum(error_sum, y1, y2):
                error = (y1 - y2) ** 2
                return error_sum + error

            # calculate errors for all algorithms
            error_sum_log, error_sum_lin, error_sum_minmax = 0, 0, 0
            for i in range(0, len(xdata)):
                error_sum_log = calculate_error_sum(error_sum_log, params[0] * np.log(xdata[i]) + params[1], ydata[i])
                error_sum_lin = calculate_error_sum(error_sum_lin, xdata[i] * all_measurements[key]['coefficient'] + all_measurements[key]['intercept'], ydata[i])
                error_sum_minmax = calculate_error_sum(error_sum_minmax, all_measurements[key]['min'], ydata[i])
                error_sum_minmax = calculate_error_sum(error_sum_minmax, all_measurements[key]['max'], ydata[i])



            s = (key + " = {" + '  "p":1, ' +
                '  "lower":' + str(minimumg_selectivity) + ',' +
                '  "upper":' + str(maximum_selectivity) +
                ', "coeff":' + str(all_measurements[key]['coefficient'][0]) +
                ', "intercept":' + str(all_measurements[key]['intercept']) +
                ', "log_coeff":' + str(params[0]) +
                ', "log_intercept":' + str(params[1]) +
                ', "best": "')

            if create_this_key and print_repository:
                if error_sum_log == min([error_sum_lin[0], error_sum_log, error_sum_minmax]):
                    s = s + "log"
                    all_measurements[key]['best']="log"
                elif error_sum_lin == min([error_sum_lin, error_sum_log, error_sum_minmax]):
                    s = s + "lin"
                    all_measurements[key]['best'] = "lin"
                elif error_sum_minmax == min([error_sum_lin, error_sum_log, error_sum_minmax]):
                    s = s + "minmax"
                    all_measurements[key]['best'] = "minmax"
                print(s + '"}')

    f.close()

    return all_measurements


def execute_generate_plots(date_id, file_identifier, plot_type, all_measurements1, all_measurements2, image_scale):
    import matplotlib.pyplot as plt
    import csv

    for key in all_measurements2: # TODO JRK: This or the other?

        if 'coefficient' in all_measurements2[key] and all_measurements2[key]['coefficient'] != 0.0 and len(all_measurements2[key]['xdata']) > 0 and len(all_measurements2[key]['ydata']) > 0: # TODO JRK: Why would it?
            my_dpi = 96
            plt.figure(figsize=(200 * image_scale / my_dpi, 150 * image_scale / my_dpi), dpi=my_dpi)
            plt.title("Selectivity profile: " + key)
            ax = plt.gca()

            # the training data
            ax.scatter(all_measurements2[key]['xdata'], all_measurements2[key]['ydata'], c="red", marker=(5, 2), s=250, label='Training data')

            # the validation data
            ax.scatter(all_measurements1[key]['xdata'], all_measurements1[key]['ydata'], c="blue", marker='+', s=250, label='Validation data')

            # baseline estimates
            default_estimator_x = []
            default_estimator_y = []
            def_est_factor_1 = 0
            def_est_factor_2 = 0
            with open('/Users/jonas/Google Drive/suite-logs/' + date_id + '/est_cards_baseline-' + date_id + '.csv',
                      'rt') as csvfile:
                spamreader = csv.reader(csvfile, delimiter=';', quotechar='|')
                for row in spamreader:
                    # read the baseline estimates to calculate the baseline selectivities
                    if (row[1].split('-')[0] == key):

                        default_estimator_x.append(
                            (int(row[2].replace(',', '')) * int(row[3].replace(',', ''))) ** (1 / 2))
                        default_estimator_y.append(int(row[5].replace(',', '')) / int(row[2].replace(',', '')) * 1.0)
                        def_est_factor_1 = int(row[5].replace(',', '')) / int(row[2].replace(',', '')) * 1.0

                        default_estimator_x.append(
                            (int(row[2].replace(',', '')) * int(row[3].replace(',', ''))) ** (1 / 2))
                        default_estimator_y.append(int(row[6].replace(',', '')) / int(row[3].replace(',', '')) * 1.0)
                        def_est_factor_2 = int(row[6].replace(',', '')) / int(row[3].replace(',', '')) * 1.0

            csvfile.close()



            # ax.scatter(default_estimator_x, default_estimator_y, c="green", marker=(3, 2), s=250, label='Default estimator')

            # max value of x axis for function plotting
            max_baseline = 0
            if len(default_estimator_x) > 0:
                max_baseline = max(default_estimator_x)

            max_card = max([all_measurements2[key]['max_card'], all_measurements1[key]['max_card'], max_baseline])


            x = np.linspace(0, max_card, 100)
            y = [def_est_factor_1] * 100
            plt.plot(x, y, "g--", label='Default estimator')
            x = np.linspace(0, max_card, 100)
            y = [def_est_factor_2] * 100
            plt.plot(x, y, "g--")

            ax.set_xscale('log')
            plt.xlim([0, max_card*1.2])
            plt.ylim(ymin=0)
            plt.xlabel("Input cardinality")
            plt.ylabel("Operator selectivity")

            best=""
            if "best"==plot_type:
                best = all_measurements2[key]['best']

            if "lin" in plot_type or best=="lin":
                # the linear function estimator
                x = np.linspace(0, max_card, 100)
                y = x * all_measurements2[key]['coefficient'] + all_measurements2[key]['intercept']
                plt.plot(x, y, "k--", label='"Linear" estimator')
            if "minmax" in plot_type or best=="minmax":
                x = np.linspace(0, max_card, 100)
                y = 100 * [all_measurements2[key]['min']]
                plt.plot(x, y, "m--", label='"Minmax" estimator')
                x = np.linspace(0, max_card, 100)
                y = 100 * [all_measurements2[key]['max']]
                plt.plot(x, y, "m--")
            if "log" in plot_type or best=="log":
                x = np.linspace(0.0001, max_card, 100)
                y = all_measurements2[key]['log_coeff'] * np.log(x) + all_measurements2[key]['log_intercept']
                plt.plot(x, y, "c--", label='"Log function" estimator')


            plt.legend(loc='best', shadow=True, framealpha=1.0, edgecolor='k')

            # save image file
            from os.path import expanduser
            home = expanduser("~")
            plt.savefig(
                home + '/Google Drive/suite-logs/' + date_id + '/' + file_identifier + '-' + date_id + '-' + key + '.png',
                dpi=my_dpi)
            plt.close()


def main():

    ## sys.argv[1] : validation data file
    ## sys.argv[2] : training data file
    ## sys.argv[3] : date_id
    ## sys.argv[4] = "generate_plots"
    ## sys.argv[5] : image file identifier
    ## sys.argv[6] : can contain log, minmax and linear
    ## sys.argv[7] : image_scale

    # training data
    all_measurements2 = read_and_process_json(file=sys.argv[2], print_repository=True)

    generate_plots = sys.argv[4]
    if generate_plots == "generate_plots":
        # baseline / validation data
        all_measurements1 = read_and_process_json(file=sys.argv[1], print_repository=False)
        execute_generate_plots(date_id=sys.argv[3], file_identifier=sys.argv[5], plot_type=sys.argv[6], all_measurements1=all_measurements1, all_measurements2=all_measurements2, image_scale=int(sys.argv[7]))


if __name__ == "__main__":
    main()