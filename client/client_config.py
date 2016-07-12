#
# Copyright (c) 2016 Intel Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import os
import json
import urlparse

VCAP_SERVICES = 'VCAP_SERVICES'
FILE_NAME = 'shuttle_scale_cut_val.csv'


class Config(object):
    """
    Reads the configuration from environment variables set by
    Cloud Foundry (CF) or by the developer when running locally.
    """
    def __init__(self, external_gateway=None, use_https=False):
        if use_https:
            proxy_env_name = 'https_proxy'
        else:
            proxy_env_name = 'http_proxy'
        if external_gateway:
            self.uri = self._get_uri(external_gateway=external_gateway)
        else:
            self.uri = self._get_uri()
        self.http_proxy_config = self._get_proxy_config(proxy_env_name)
        self.file_path = self._get_file_path(FILE_NAME)

    @staticmethod
    def _get_uri(external_gateway=None):
        """
        Args:
          external_gateway(string): URL to space-shuttle-gateway which
            can be specified when running locally.

        Returns:
          string: websocket uri constructed from env variable or command
            line argument if specified.

        Raises:
          ConfigEnvError: When VCAP_SERVICES is not present and the app
          can't run raised.
        """
        if external_gateway:
                return 'wss://' + external_gateway + '/ws'
        try:
            service_config = json.loads(os.environ[VCAP_SERVICES])
            return 'wss://' + service_config['gateway'][0]['credentials']['url'] + '/ws'
        except KeyError:
            raise ConfigEnvError('Environment variable that should contain '
                                 'configuration is missing or incorrect.')

    @staticmethod
    def _get_proxy_config(proxy_env_name):
        """
        Returns proxy host and port if found in VCAP_SERVICES.

        Args:
            proxy_env_name(string): name of env variable storing proxy info.

        Returns:
            http_proxy_config (dict of: host=string, port=int):
             dict with separated http_proxy_host and http_proxy_port
        """
        proxy_env = os.getenv(proxy_env_name)
        if not proxy_env:
            return None
        parser = urlparse.urlparse(proxy_env)
        return dict(host=parser.hostname, port=parser.port)

    @staticmethod
    def _get_file_path(file_name):
        return os.path.abspath(os.path.join(os.getcwd(), file_name))


class ConfigEnvError(Exception):
    """
    Environment variable that should contain configuration is missing or incorrect.
    """
    pass
