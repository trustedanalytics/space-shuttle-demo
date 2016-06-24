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

import requests
import json
from collections import defaultdict
headers = {'Content-type': 'application/json',
'Accept': 'application/json,text/plain'}

in_file = 'shuttle_scale_cut_val.csv'
line_nmbr = 1

result_sum = defaultdict(int)

scoring_engine_url = ''  # scoring engline url

with open (in_file) as f:
    for line in f:
        result = "".join(line.strip().split(' ')[1:])
        r = requests.get(scoring_engine_url, params={'data': result}, headers=headers)
        score = float(r.text)
        result_sum[score] += 1
        print r.url
        print "get:", score, 'expect:', line[0], "stats", dict(result_sum), "line: ", line_nmbr
        if score != 1:
            print 20*'#'
        line_nmbr+=1

print "RESULT:", dict(result_sum)

