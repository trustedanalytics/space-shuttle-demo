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

import sys
import thread
import time
import argparse

from twisted.python import log
from twisted.internet import reactor
from autobahn.twisted.websocket import WebSocketClientFactory, \
                                       WebSocketClientProtocol, \
                                       connectWS

from client_config import Config


class DataStreamProtocol(WebSocketClientProtocol):
    """
    Protocol class which specifies the behaviour of the client.
    """
    def onOpen(self):
        """
        Open connection handling.
        """
        log.msg("Connection established")

        def run():
            log.msg("Sending data...")
            while True:
                self._send_data()
                log.msg("End of file. Reopening...")
            log.msg("Thread terminating...")

        thread.start_new_thread(run, ())

    def onClose(self, wasClean, code, reason):
        log.msg("Websocket connection closed: {0}".format(reason))

    def _send_data(self):
        with open(config.file_path, "r") as data:
            for line in data:
                log.msg(line)
                self.sendMessage("[" + line + "]")
                time.sleep(0.1)


def parse_arguments():
    parser = argparse.ArgumentParser(
        description='Deployment script for Space Shuttle client')
    parser.add_argument('--gateway-url', type=str,
                        help='gateway api url, '
                        'e.g. gateway-479613d7.demotrustedanalytics.com')
    parser.add_argument('--use-https', dest='https', action='store_true',
                        help='set of flag cause use of `https_proxy` env '
                             'instead of default `http_proxy`')
    return parser.parse_args()

if __name__ == '__main__':

    log.startLogging(sys.stdout)
    args = parse_arguments()
    config = Config(external_gateway=args.gateway_url,
                    use_https=args.https)

    uri = config.uri
    proxy_config = config.http_proxy_config

    factory = WebSocketClientFactory(uri, proxy=proxy_config)
    factory.protocol = DataStreamProtocol

    connectWS(factory)
    reactor.run()
