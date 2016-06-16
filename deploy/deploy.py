#
# Copyright (c) 2016 Intel Corporation
#
# Licensed under the Apache License, Version 2.0 (the 'License');
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an 'AS IS' BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

"""
This scripts automates deployment of space-shuttle-demo application
(creates required service instances, uploads model to HDFS and pushes
application to Cloud Foundry using manifest file).
"""

import json

from app_deployment_helpers import cf_cli
from app_deployment_helpers import cf_helpers

PARSER = cf_helpers.get_parser("space-shuttle-demo")
PARSER.add_argument('--path_to_model', type=str,
                    help='Path to a model for scoring-engine on local '
                         'machine, eg. /path/to/model/model.tar, '
                         'default path is "./model.tar"')
ARGS = PARSER.parse_args()

CF_INFO = cf_helpers.get_info(ARGS)
cf_cli.login(CF_INFO)

cf_cli.create_service('influxdb088', 'free', 'space-shuttle-db')
cf_cli.create_service('zookeeper', 'shared', 'zookeeper')
cf_cli.create_service('gateway', 'Simple', 'space-shuttle-gateway')

LOCAL_MODEL_PATH = ARGS.path_to_model if ARGS.path_to_model else 'model.tar'
HDFS_MODEL_PATH = cf_helpers.upload_to_hdfs(ARGS.base_url, CF_INFO.org,
                                            LOCAL_MODEL_PATH,
                                            'model')
cf_cli.create_service('scoring-engine', 'Simple',
                      'space-shuttle-scoring-engine',
                      json.dumps({"uri": HDFS_MODEL_PATH}))

PROJECT_DIR = ARGS.project_dir if ARGS.project_dir else \
    cf_helpers.get_project_dir()
cf_helpers.prepare_package(work_dir=PROJECT_DIR)
cf_helpers.push(work_dir=PROJECT_DIR, options=ARGS.app_name)

