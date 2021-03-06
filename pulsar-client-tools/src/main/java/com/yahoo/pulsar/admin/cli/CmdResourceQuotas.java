/**
 * Copyright 2016 Yahoo Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yahoo.pulsar.admin.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.yahoo.pulsar.client.admin.PulsarAdmin;
import com.yahoo.pulsar.client.admin.PulsarAdminException;
import com.yahoo.pulsar.common.policies.data.ResourceQuota;

@Parameters(commandDescription = "Operations about resource quotas")
public class CmdResourceQuotas extends CmdBase {

    @Parameters(commandDescription = "Get the resource quota for specified namespace bundle, or default quota if no namespace/bundle specified.")
    private class GetResourceQuota extends CliCommand {

        @Parameter(names = { "--namespace",
                "-n" }, description = "property/cluster/namespace, must be specified together with '--bundle'\n")
        private java.util.List<String> names;

        @Parameter(names = { "--bundle",
                "-b" }, description = "{start-boundary}_{end-boundary}, must be specified together with '--namespace'\n")
        private String bundle;

        @Override
        void run() throws PulsarAdminException, ParameterException {
            if (bundle == null && names == null) {
                print(admin.resourceQuotas().getDefaultResourceQuota());
            } else if (bundle != null && names != null) {
                String namespace = validateNamespace(names);
                print(admin.resourceQuotas().getNamespaceBundleResourceQuota(namespace, bundle));
            } else {
                throw new ParameterException("namespace and bundle must be provided together.");
            }
        }
    }

    @Parameters(commandDescription = "Set the resource quota for specified namespace bundle, or default quota if no namespace/bundle specified.")
    private class SetResourceQuota extends CliCommand {

        @Parameter(names = { "--namespace",
                "-n" }, description = "property/cluster/namespace, must be specified together with '--bundle'\n")
        private java.util.List<String> names;

        @Parameter(names = { "--bundle",
                "-b" }, description = "{start-boundary}_{end-boundary}, must be specified together with '--namespace'\n")
        private String bundle;

        @Parameter(names = { "--msgRateIn",
                "-mi" }, description = "expected incoming messages per second\n", required = true)
        private long msgRateIn = 0;

        @Parameter(names = { "--msgRateOut",
                "-mo" }, description = "expected outgoing messages per second\n", required = true)
        private long msgRateOut = 0;

        @Parameter(names = { "--bandwidthIn",
                "-bi" }, description = "expected inbound bandwidth (bytes/second)\n", required = true)
        private long bandwidthIn = 0;

        @Parameter(names = { "--bandwidthOut",
                "-bo" }, description = "expected outbound bandwidth (bytes/second)\n", required = true)
        private long bandwidthOut = 0;

        @Parameter(names = { "--memory", "-mem" }, description = "expected memory usage (Mbytes)\n", required = true)
        private long memory = 0;

        @Parameter(names = { "--dynamic",
                "-d" }, description = "dynamic (allow to be dynamically re-calculated) or not\n")
        private boolean dynamic = false;

        @Override
        void run() throws PulsarAdminException {
            ResourceQuota quota = new ResourceQuota();
            quota.setMsgRateIn(msgRateIn);
            quota.setMsgRateOut(msgRateOut);
            quota.setBandwidthIn(bandwidthIn);
            quota.setBandwidthOut(bandwidthOut);
            quota.setMemory(memory);
            quota.setDynamic(dynamic);

            if (bundle == null && names == null) {
                admin.resourceQuotas().setDefaultResourceQuota(quota);
            } else if (bundle != null && names != null) {
                String namespace = validateNamespace(names);
                admin.resourceQuotas().setNamespaceBundleResourceQuota(namespace, bundle, quota);
            } else {
                throw new ParameterException("namespace and bundle must be provided together.");
            }
        }
    }

    @Parameters(commandDescription = "Reset the specified namespace bundle's resource quota to default value.")
    private class ResetNamespaceBundleResourceQuota extends CliCommand {

        @Parameter(names = { "--namespace", "-n" }, description = "property/cluster/namespace\n", required = true)
        private java.util.List<String> names;

        @Parameter(names = { "--bundle", "-b" }, description = "{start-boundary}_{end-boundary}\n", required = true)
        private String bundle;

        @Override
        void run() throws PulsarAdminException {
            String namespace = validateNamespace(names);
            admin.resourceQuotas().resetNamespaceBundleResourceQuota(namespace, bundle);
        }
    }

    CmdResourceQuotas(PulsarAdmin admin) {
        super("resource-quotas", admin);
        jcommander.addCommand("get", new GetResourceQuota());
        jcommander.addCommand("set", new SetResourceQuota());
        jcommander.addCommand("reset-namespace-bundle-quota", new ResetNamespaceBundleResourceQuota());
    }
}
