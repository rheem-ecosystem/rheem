# Selectivity Repository

## 1. Installing python dependencies

The dependecies are listed in `requirements.txt`. Install using `pip`, e.g. with `pip install -r requirements.txt`.

## 2. Annotate Rheem operators

In a Rheem plan, annotate your selection of ReduceBy, FlatMap, Filter, Distinct and Join operators with a unique identifier such as the following:

```.flatMap(_.split("\\W+"),
   udfSelectivity = ProbabilisticDoubleInterval.createFromSpecification(
   "my.flatmap", configuration
   ),
   udfSelectivityKey = "my.flatmap"
   ).withName("my.flatmap")```

(TODO JRK: Annotating three times does not make sense)

## 3. Execute Rheem plan

## 4. Extract Selectivities from execution logs

`selectivity-repository.py` takes the following inputs:

```    ## sys.argv[1] : validation data file
    ## sys.argv[2] : training data file
    ## sys.argv[3] : date_id
    ## sys.argv[4] = "generate_plots"
    ## sys.argv[5] : image file identifier
    ## sys.argv[6] : can contain log, minmax and linear
    ## sys.argv[7] : image_scale```

(TODO JRK: Cleanup input arguments.)

A basic command currently looks as follows:

`python3 selectivity-repository.py not_required_for_basic_use path/to/executions.json not_required_for_basic_use dont_generate_plots`

The command will output the selectivity profile estimates that can be copy & pasted into the .properties file of the regarding Rheem plan. Rheem will use these updated selectivity estimates in subsequent executions of this plan.
