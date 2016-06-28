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

HOST="localhost"
PORT="1883"
TOPIC="space-shuttle/test-data"
echo "Enter username:"
read USER
echo "Enter password:"
read -s PASS
while read p; echo  "$p"; mosquitto_pub -d -t ${TOPIC} -m "$p" -u ${USER} -P ${PASS} -h ${HOST} -p ${PORT}; do sleep 1; done < shuttle_scale_cut_val.csv
