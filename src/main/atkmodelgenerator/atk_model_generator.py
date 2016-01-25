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

import trustedanalytics as ta
import sys

ta.create_credentials_file('credentials')
ta.connect()
print 'ta.server: %s' % ta.server
ds = sys.argv[1]
sc = [("label", ta.float64), ("feature1", ta.float64), ("feature2", ta.float64), ("feature3", ta.float64), ("feature4", ta.float64), ("feature5", ta.float64), ("feature6", ta.float64), ("feature7", ta.float64), ("feature8", ta.float64), ("feature9", ta.float64)]
csv = ta.CsvFile(ds, sc, ',', 0)

frame = ta.Frame(csv)
m = ta.LibsvmModel(name='model_name')
m.train(frame, "label", ["feature1", "feature2", "feature3", "feature4", "feature5", "feature6", "feature7", "feature8", "feature9"], epsilon=0.000001, degree=3, gamma=0.11, coef=0.0, nu=0.0001, cache_size=100.0, shrinking=1, probability=0, c=1.0, p=0.1, nr_weight=0)

print m.publish()
