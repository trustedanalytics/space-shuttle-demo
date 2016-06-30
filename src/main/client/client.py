#
# Copyright (c) 2015 Intel Corporation
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

import websocket
import thread
import time
import sys
import os
import ssl


def on_message(ws, message):
    print "### message ###"
    print message


def on_error(ws, error):
    print "### error ###"
    print error


def on_close(ws):
    print "### closed ###"


def on_open(ws):
    print "### open ###"

    def run():
        print "### send data ####"
        sendData(ws)
        ws.close()
        print "thread terminating..."

    thread.start_new_thread(run, ())


def sendData(ws):
    with open(sys.argv[2], "r") as data:
        for line in data:
            print "send: " + line
            ws.send("[" + line + "]")
            time.sleep(0.1)


def get_proxy(http_proxy):
    if not http_proxy:
        return (None, None)

    id = http_proxy.find('://')
    if id != -1:
        http_proxy = http_proxy[id + 3:]
    return http_proxy.split(":")


if __name__ == "__main__":

    http_host, http_port = get_proxy(os.getenv("http_proxy"))

    uri = sys.argv[1]

    websocket.enableTrace(True)
    ws = websocket.WebSocketApp(uri,
                                on_message=on_message,
                                on_error=on_error,
                                on_close=on_close)
    ws.on_open = on_open

    sslopt = {"cert_reqs": ssl.CERT_NONE, "check_hostname": False}

    if http_host:
        ws.run_forever(http_proxy_host=http_host, http_proxy_port=http_port, sslopt=sslopt)
    else:
        sslopt = {"cert_reqs": ssl.CERT_NONE, "check_hostname": False}
        ws.run_forever(sslopt=sslopt)
