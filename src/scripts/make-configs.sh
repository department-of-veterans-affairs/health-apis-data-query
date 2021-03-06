#! /usr/bin/env bash

usage() {
  cat <<EOF

$0 [options]

Generate configurations for local development.

Options
     --debug               Enable debugging
 -h, --help                Print this help and exit.
     --secrets-conf <file> The configuration file with secrets!

Secrets Configuration
 This bash file is sourced. These may optionally be provided
 - DATAQUERY_DB_PASSWORD
 - DATAQUERY_DB_URL
 - DATAQUERY_DB_USER
 - IDS_ENCODING_KEY
 - IDS_PATIENT_ID_PATTERN
 - WEB_EXCEPTION_KEY

$1
EOF
  exit 1
}

REPO=$(cd $(dirname $0)/../.. && pwd)
SECRETS="$REPO/secrets.conf"
PROFILE=dev
MARKER=$(date +%s)
ARGS=$(getopt -n $(basename ${0}) \
    -l "debug,help,secrets-conf:" \
  -o "h" -- "$@")
[ $? != 0 ] && usage
eval set -- "$ARGS"
while true
do
  case "$1" in
    --debug) set -x ;;
    -h|--help) usage "halp! what this do?" ;;
    --secrets-conf) SECRETS="$2" ;;
    --) shift;break ;;
  esac
  shift;
done

echo "Loading secrets: $SECRETS"
[ ! -f "$SECRETS" ] && usage "File not found: $SECRETS"
. $SECRETS

MISSING_SECRETS=false
[ -z "$DATAQUERY_DB_PASSWORD" ] && DATAQUERY_DB_PASSWORD='<YourStrong!Passw0rd>'
[ -z "$DATAQUERY_DB_URL" ] && DATAQUERY_DB_URL='jdbc:sqlserver://localhost:1433;database=dq;sendStringParametersAsUnicode=false'
[ -z "$DATAQUERY_DB_USER" ] && DATAQUERY_DB_USER='SA'
[ -z "$IDS_ENCODING_KEY" ] && IDS_ENCODING_KEY=data-query
[ -z "$IDS_PATIENT_ID_PATTERN" ] && IDS_PATIENT_ID_PATTERN="[0-9]+(V[0-9]{6})?"
[ -z "$WEB_EXCEPTION_KEY" ] && WEB_EXCEPTION_KEY="-shanktopus-for-the-win-"
[ $MISSING_SECRETS == true ] && usage "Missing configuration secrets, please update $SECRETS"

makeConfig() {
  local project="$1"
  local profile="$2"
  local target="$REPO/$project/config/application-${profile}.properties"
  [ -f "$target" ] && mv -v $target $target.$MARKER
  grep -E '(.*= *unset)' "$REPO/$project/src/main/resources/application.properties" \
    > "$target"
}

addValue() {
  local project="$1"
  local profile="$2"
  local key="$3"
  local value="$4"
  local target="$REPO/$project/config/application-${profile}.properties"
  local escapedValue=$(echo $value | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')
  echo "$key=$escapedValue" >> $target
}

configValue() {
  local project="$1"
  local profile="$2"
  local key="$3"
  local value="$4"
  local target="$REPO/$project/config/application-${profile}.properties"
  local escapedValue=$(echo $value | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')
  sed -i "s/^$key=.*/$key=$escapedValue/" $target
}

checkForUnsetValues() {
  local project="$1"
  local profile="$2"
  local target="$REPO/$project/config/application-${profile}.properties"
  echo "checking $target"
  grep -E '(.*= *unset)' "$target"
  [ $? == 0 ] && echo "Failed to populate all unset values" && exit 1
  diff -q $target $target.$MARKER
  [ $? == 0 ] && rm -v $target.$MARKER
}

comment() {
  local project="$1"
  local profile="$2"
  local target="$REPO/$project/config/application-${profile}.properties"  
  cat >> $target
}

whoDis() {
  local me=$(git config --global --get user.name)
  [ -z "$me" ] && me=$USER
  echo $me
}

sendMoarSpams() {
  local spam=$(git config --global --get user.email)
  [ -z "$spam" ] && spam=$USER@aol.com
  echo $spam
}

makeConfig data-query $PROFILE
addValue data-query $PROFILE ids-client.encoded-ids.encoding-key "$IDS_ENCODING_KEY"
addValue data-query $PROFILE ids-client.patient-icn.id-pattern "$IDS_PATIENT_ID_PATTERN"
configValue data-query $PROFILE data-query.public-url http://localhost:8090
configValue data-query $PROFILE data-query.public-web-exception-key "$WEB_EXCEPTION_KEY"
configValue data-query $PROFILE metadata.statement-type patient
configValue data-query $PROFILE metadata.contact.name "$(whoDis)"
configValue data-query $PROFILE metadata.contact.email "$(sendMoarSpams)"
configValue data-query $PROFILE metadata.security.token-endpoint http://fake.com/token
configValue data-query $PROFILE metadata.security.authorize-endpoint http://fake.com/authorize
configValue data-query $PROFILE metadata.security.management-endpoint http://fake.com/manage
configValue data-query $PROFILE metadata.security.revocation-endpoint http://fake.com/revoke
configValue data-query $PROFILE spring.datasource.url "$DATAQUERY_DB_URL"
configValue data-query $PROFILE spring.datasource.username "$DATAQUERY_DB_USER"
configValue data-query $PROFILE spring.datasource.password "$DATAQUERY_DB_PASSWORD"
configValue data-query $PROFILE well-known.capabilities "context-standalone-patient, launch-ehr, permission-offline, permission-patient"
configValue data-query $PROFILE well-known.response-type-supported "code, refresh_token"
configValue data-query $PROFILE well-known.scopes-supported "patient/DiagnosticReport.read, patient/Patient.read, offline_access"
addValue data-query $PROFILE dynamo-patient-registrar.enabled false
addValue data-query $PROFILE dynamo-patient-registrar.endpoint "http://localhost:8000"
addValue data-query $PROFILE dynamo-patient-registrar.table patient-registration-local
addValue data-query $PROFILE dynamo-patient-registrar.region us-gov-west-1
checkForUnsetValues data-query $PROFILE

comment data-query $PROFILE <<EOF
#
# To use AWS, use the following values and set
# environment variables: AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY
#
#dynamo-patient-registrar.endpoint=https://dynamodb.us-gov-west-1.amazonaws.com
#dynamo-patient-registrar.table=patient-registration-qa
#dynamo-patient-registrar.region=us-gov-west-1
EOF
