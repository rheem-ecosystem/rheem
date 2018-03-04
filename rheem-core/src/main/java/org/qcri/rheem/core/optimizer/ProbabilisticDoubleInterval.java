package org.qcri.rheem.core.optimizer;

import org.json.JSONObject;
import org.qcri.rheem.core.api.Configuration;
import org.qcri.rheem.core.api.exception.RheemException;
import org.qcri.rheem.core.optimizer.costs.LoadProfileEstimators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

/***
 * An value representation that is capable of expressing uncertainty.
 * It addresses uncertainty by expressing estimates as intervals and assigning a probability of correctness (in [0, 1]).
 */
public class ProbabilisticDoubleInterval {

    private static final Logger logger = LoggerFactory.getLogger(LoadProfileEstimators.class);

    /**
     * Instance that basically represents the value {@code 0d}.
     */
    public static final ProbabilisticDoubleInterval zero = ProbabilisticDoubleInterval.ofExactly(0d);

    /**
     * Probability of correctness between in the interval [0, 1]. This helps
     * Rheem in situations with many estimates to pick the best one.
     */
    private final double correctnessProb;

    /**
     * Lower and upper estimate. Not that this is not a bounding box, i.e., there is no guarantee that the finally
     * observed value will be within the estimated interval.
     */
    private final double lowerEstimate, upperEstimate;

    /**
     * When merging instances somehow, overriding instance should be chosen over the others.
     */
    private final boolean isOverride;

    private final String keyString;
    private final double coeff;
    private final double intercept;
    private final double log_coeff;
    private final double log_intercept;
    private final String best;

    /**
     * Creates a new instance with a zero-width interval and a confidence of {@code 1}.
     *
     * @param value lower and upper value
     * @return the new instance
     */
    public static ProbabilisticDoubleInterval ofExactly(double value) {
        return new ProbabilisticDoubleInterval(value, value, 1d);
    }

    public ProbabilisticDoubleInterval(double lowerEstimate, double upperEstimate, double correctnessProb) {
        this(lowerEstimate, upperEstimate, correctnessProb, false);
    }

    public ProbabilisticDoubleInterval(double lowerEstimate, double upperEstimate, double correctnessProb, boolean isOverride) {
        assert lowerEstimate <= upperEstimate : String.format("%f > %f, which is illegal.", lowerEstimate, upperEstimate);
        assert correctnessProb >= 0 && correctnessProb <= 1 : String.format("Illegal probability %f.", correctnessProb);

        this.correctnessProb = correctnessProb;
        this.lowerEstimate = lowerEstimate;
        this.upperEstimate = upperEstimate;
        this.isOverride = isOverride;

        this.keyString = "";
        this.coeff = 0;
        this.intercept = 0;
        this.log_coeff = 0;
        this.log_intercept = 0;
        this.best = "";
    }

    public ProbabilisticDoubleInterval(double lowerEstimate, double upperEstimate, double correctnessProb, boolean isOverride, String keyString, double coeff, double intercept, double log_coeff, double log_intercept, String best) {
        this.keyString = keyString;
        assert lowerEstimate <= upperEstimate : String.format("%f > %f, which is illegal.", lowerEstimate, upperEstimate);
        assert correctnessProb >= 0 && correctnessProb <= 1 : String.format("Illegal probability %f.", correctnessProb);

        this.correctnessProb = correctnessProb;
        this.lowerEstimate = lowerEstimate;
        this.upperEstimate = upperEstimate;
        this.isOverride = isOverride;
        this.coeff = coeff;
        this.intercept = intercept;
        this.log_coeff = log_coeff;
        this.log_intercept = log_intercept;
        this.best = best;
    }


    public static ProbabilisticDoubleInterval createFromSpecification(String configKey, Configuration configuration) {
//        final LoadProfileEstimator cachedEstimator =
//                configuration.getLoadProfileEstimatorCache().optionallyProvideFor(configKey).orElse(null);
//        if (cachedEstimator != null) return cachedEstimator.copy(); // TODO JRK caching might be necessary

        String[] split = configKey.split("-");
        configKey = split[0];

        final Optional<String> optSpecification = configuration.getOptionalStringProperty(configKey);
        if (optSpecification.isPresent()) {
            final ProbabilisticDoubleInterval interval;
            String mode = configuration.getStringProperty("rheem.optimizer.sr.mode", "best");
            interval = ProbabilisticDoubleInterval.createFromSpecification(configKey, optSpecification.get(), mode);
//            configuration.getLoadProfileEstimatorCache().set(configKey, estimator.copy());
            return interval;
        } else {
            logger.warn("Could not find an selectivity specification associated with '{}'.", configuration);
            return null;
        }
    }

    public static ProbabilisticDoubleInterval createFromSpecification(String configKey, String specification, String mode) {
        String[] split = configKey.split("-");
        configKey = split[0];
        try {
            final JSONObject spec = new JSONObject(specification);
            if (!spec.has("type") || "juel".equalsIgnoreCase(spec.getString("type"))) {
                return createFromJuelSpecification(configKey, spec, mode);
            } else {
                throw new RheemException(String.format("Unknown specification type: %s", spec.get("type")));
            }
        } catch (Exception e) {
            throw new RheemException(String.format("Could not initialize from specification \"%s\".", specification), e);
        }
    }

    public static ProbabilisticDoubleInterval createFromJuelSpecification(String configKey, JSONObject spec, String mode) {
        double correctnessProb = spec.getDouble("p");
        double lower = spec.getDouble("lower");
        double upper = spec.getDouble("upper");
        double coeff = spec.getDouble("coeff");
        double intercept = spec.getDouble("intercept");
        double log_coeff = spec.getDouble("log_coeff");
        double log_intercept = spec.getDouble("log_intercept");
        String best;
        if (mode.equals("best")) {
            best = spec.getString("best");
        } else {
            best = mode;
        }


        return new ProbabilisticDoubleInterval(lower, upper, correctnessProb, configKey, coeff, intercept, log_coeff, log_intercept, best);
    }

    public ProbabilisticDoubleInterval(double lowerEstimate, double upperEstimate, double correctnessProb, String keyString, double coeff, double intercept, double log_coeff, double log_intercept, String best) {
        this(lowerEstimate, upperEstimate, correctnessProb, false, keyString, coeff, intercept, log_coeff, log_intercept, best);
    }

    public double getLowerEstimate() {
        return this.lowerEstimate;
    }

    public double getUpperEstimate() {
        return this.upperEstimate;
    }

    public double getCoeff() {
        return this.coeff;
    }

    public double getIntercept() {
        return this.intercept;
    }

    public double getLog_coeff() {
        return this.log_coeff;
    }

    public double getLog_intercept() {
        return this.log_intercept;
    }

    public String getBest() {
        return this.best;
    }

    public double getAverageEstimate() {
        return (this.getUpperEstimate() + this.getLowerEstimate()) / 2;
    }

    public long getGeometricMeanEstimate() {
        return Math.round(Math.pow(this.getLowerEstimate() * this.getUpperEstimate(), 0.5));
    }

    public double getCorrectnessProbability() {
        return this.correctnessProb;
    }

    /**
     * Checks whether this instance is an exact estimate of the given value.
     *
     * @param exactEstimate the hypothesized exact estimation value
     * @return whether this instance is exactly {@code exactEstimate}
     */
    public boolean isExactly(long exactEstimate) {
        return this.correctnessProb == 1d && this.lowerEstimate == this.upperEstimate && this.upperEstimate == exactEstimate;
    }

    /**
     * Creates a new instance that represents the sum of the {@code this} and {@code that} instance.
     *
     * @param that the other summand
     * @return the sum
     */
    public ProbabilisticDoubleInterval plus(ProbabilisticDoubleInterval that) {
        return new ProbabilisticDoubleInterval(
                this.getLowerEstimate() + that.getLowerEstimate(),
                this.getUpperEstimate() + that.getUpperEstimate(),
                Math.min(this.getCorrectnessProbability(), that.getCorrectnessProbability())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ProbabilisticDoubleInterval estimate = (ProbabilisticDoubleInterval) o;
        return Double.compare(estimate.correctnessProb, this.correctnessProb) == 0 &&
                this.lowerEstimate == estimate.lowerEstimate &&
                this.upperEstimate == estimate.upperEstimate;
    }

    /**
     * Compares with this instance equals with {@code that} instance within given delta bounds.
     */
    public boolean equalsWithinDelta(ProbabilisticDoubleInterval that,
                                     double probDelta,
                                     long lowerEstimateDelta,
                                     long upperEstimateDelta) {
        return Math.abs(that.correctnessProb - this.correctnessProb) <= probDelta &&
                Math.abs(this.lowerEstimate - that.lowerEstimate) <= lowerEstimateDelta &&
                Math.abs(this.upperEstimate - that.upperEstimate) <= upperEstimateDelta;
    }

    public boolean isOverride() {
        return this.isOverride;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.correctnessProb, this.lowerEstimate, this.upperEstimate);
    }

    @Override
    public String toString() {
        String s = String.format("(%,.2f..%,.2f ~ %.1f%%)",
                this.lowerEstimate, this.upperEstimate, this.correctnessProb * 100d);
        s = s.concat(" selecitvityKey: " + this.keyString);
        return s;
    }

}
