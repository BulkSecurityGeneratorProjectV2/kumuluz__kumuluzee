/*
 *  Copyright (c) 2014-2019 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kumuluz.ee.configuration.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import com.kumuluz.ee.configuration.ConfigurationDecoder;
import com.kumuluz.ee.configuration.ConfigurationSource;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class ConfigurationImpl {

    private Logger utilLogger;
    private ConfigurationDispatcher dispatcher;
    private List<ConfigurationSource> configurationSources;
    private ConfigurationDecoder configurationDecoder;

    public ConfigurationImpl() {
        init();
    }

    private void init() {
        // specify sources
        configurationSources = new ArrayList<>();

        ServiceLoader.load(ConfigurationSource.class).forEach(configurationSources::add);

        dispatcher = new ConfigurationDispatcher();

        configurationSources.forEach(configurationSource -> configurationSource.init(dispatcher));

        // initialise configuration decoder
        List<ConfigurationDecoder> configurationDecoders = new ArrayList<>();
        ServiceLoader.load(ConfigurationDecoder.class).forEach(configurationDecoders::add);
        if (configurationDecoders.size() > 1) {
            throw new IllegalStateException(
                "There is more than one service provider defined for the ConfigurationDecoder interface.");
        } else if (configurationDecoders.size() == 1) {
            configurationDecoder = configurationDecoders.get(0);
        }
    }

    public void postInit() {
        configurationSources.forEach(ConfigurationSource::postInit);
        utilLogger = Logger.getLogger(ConfigurationUtil.class.getName());
    }

    public Boolean isUtilLoggerAvailable() {
        return utilLogger != null;
    }

    public Logger getUtilLogger() {
        return utilLogger;
    }

    public ConfigurationDispatcher getDispatcher() {
        return dispatcher;
    }

    public List<ConfigurationSource> getConfigurationSources() {
        return configurationSources;
    }

    public ConfigurationDecoder getConfigurationDecoder() {
        return configurationDecoder;
    }
}
