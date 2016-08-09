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

set -e

VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v '\[' | tail -1)
PROJECT_NAME=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.name | grep -v '\[' | tail -1)
PACKAGE_CATALOG=${PROJECT_NAME}-${VERSION}
JAR_NAME="${PACKAGE_CATALOG}.jar"

# remove src/app-deployment-helpers to avoid license check failure
rm -rf src/app-deployment-helpers

# build project
mvn clean package -Dmaven.test.skip=true

# create tmp catalog
rm -rf ${PACKAGE_CATALOG}
mkdir ${PACKAGE_CATALOG}

# files to package
cp manifest.yml ${PACKAGE_CATALOG}
cp --parents target/${JAR_NAME} ${PACKAGE_CATALOG}

BASE_DIR=`pwd`
cp --parents atkmodelgenerator/atk_model_generator.py ${BASE_DIR}/${PACKAGE_CATALOG}
cp --parents atkmodelgenerator/*.csv ${BASE_DIR}/${PACKAGE_CATALOG}

# package deployment script
rm -rf deploy/vendor
mkdir deploy/vendor
pip install --exists-action=w --download deploy/vendor -r deploy/requirements.txt
cp --parents deploy/deploy.py ${BASE_DIR}/${PACKAGE_CATALOG}
cp --parents deploy/requirements.txt ${BASE_DIR}/${PACKAGE_CATALOG}
cp --parents deploy/tox.ini ${BASE_DIR}/${PACKAGE_CATALOG}
cp --parents -r deploy/vendor ${BASE_DIR}/${PACKAGE_CATALOG}

# space shuttle client
rm -rf client/vendor
mkdir client/vendor
pip install --exists-action=w --download client/vendor -r client/requirements.txt --no-use-wheel
cp --parents -r client/vendor ${BASE_DIR}/${PACKAGE_CATALOG}
cp --parents client/space_shuttle_client.py ${BASE_DIR}/${PACKAGE_CATALOG}
cp --parents client/client_config.py ${BASE_DIR}/${PACKAGE_CATALOG}
cp --parents client/tox.ini ${BASE_DIR}/${PACKAGE_CATALOG}
cp --parents client/requirements.txt ${BASE_DIR}/${PACKAGE_CATALOG}
cp --parents client/manifest.yml ${BASE_DIR}/${PACKAGE_CATALOG}
cp --parents client/*.csv ${BASE_DIR}/${PACKAGE_CATALOG}

# download scoring engine model
wget https://s3.amazonaws.com/trustedanalytics/v0.7.1/models/space-shuttle-model.tar
cp space-shuttle-model.tar ${BASE_DIR}/${PACKAGE_CATALOG}

# prepare build manifest
echo "commit_sha=$(git rev-parse HEAD)" > ${PACKAGE_CATALOG}/build_info.ini

# create zip package
cd ${PACKAGE_CATALOG}
rm -f ../${PROJECT_NAME}-${VERSION}.zip
zip -r ../${PROJECT_NAME}-${VERSION}.zip *
cd ..

# remove tmp catalog
rm -r ${PACKAGE_CATALOG}

echo "Zip package for $PROJECT_NAME project in version $VERSION has been prepared."
