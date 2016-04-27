package org.qcri.rheem.core.test;

import org.qcri.rheem.core.plan.executionplan.Channel;
import org.qcri.rheem.core.plan.rheemplan.OutputSlot;
import org.qcri.rheem.core.platform.ChannelDescriptor;

/**
 * Dummy {@link Channel}.
 */
public class DummyReusableChannel extends Channel {

    public static final ChannelDescriptor DESCRIPTOR = new ChannelDescriptor(
            DummyReusableChannel.class,
            true,
            true,
            false
    );

    public DummyReusableChannel(ChannelDescriptor descriptor, OutputSlot<?> producerSlot) {
        super(descriptor, producerSlot);
        assert DESCRIPTOR == descriptor;
    }

    @Override
    public Channel copy() {
        throw new UnsupportedOperationException();
    }
}
