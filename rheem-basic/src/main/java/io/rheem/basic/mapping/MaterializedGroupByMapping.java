package io.rheem.basic.mapping;

import io.rheem.basic.operators.GroupByOperator;
import io.rheem.basic.operators.MaterializedGroupByOperator;
import io.rheem.core.mapping.Mapping;
import io.rheem.core.mapping.OperatorPattern;
import io.rheem.core.mapping.PlanTransformation;
import io.rheem.core.mapping.ReplacementSubplanFactory;
import io.rheem.core.mapping.SubplanMatch;
import io.rheem.core.mapping.SubplanPattern;
import io.rheem.core.plan.rheemplan.Operator;
import io.rheem.core.types.DataSetType;

import java.util.Collection;
import java.util.Collections;

/**
 * This mapping translates the {@link GroupByOperator} into the {@link MaterializedGroupByOperator}.
 */
public class MaterializedGroupByMapping implements Mapping {


    @Override
    public Collection<PlanTransformation> getTransformations() {
        return Collections.singleton(new PlanTransformation(this.createSubplanPattern(), new ReplacementFactory()));
    }

    private SubplanPattern createSubplanPattern() {
        final OperatorPattern groupByPattern = new OperatorPattern<>(
                "groupBy",
                new GroupByOperator<>(
                        null,
                        DataSetType.none(),
                        DataSetType.groupedNone()
                ),
                false);
        return SubplanPattern.createSingleton(groupByPattern);
    }

    private static class ReplacementFactory extends ReplacementSubplanFactory {

        @Override
        protected Operator translate(SubplanMatch subplanMatch, int epoch) {
            final GroupByOperator groupBy = (GroupByOperator) subplanMatch.getMatch("groupBy").getOperator();

            return new MaterializedGroupByOperator<>(
                    groupBy.getKeyDescriptor(),
                    groupBy.getInputType(),
                    groupBy.getOutputType()
            ).at(epoch);
        }
    }


}
