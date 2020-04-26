s2i build . registry.redhat.io/redhat-openjdk-18/openjdk18-openshift:latest quay.io/mgreenbe/httpserverdemo:1
docker push quay.io/mgreenbe/httpserverdemo:1
# make repo public

oc process -f httpserverdemo.yml | oc create -f -


GW_PROJECT=3scale
OCP_WILDCARD_DOMAIN=amp.apps-crc.testing

oc delete route/httpserverdemo-staging-route -n $GW_PROJECT
oc delete route/httpserverdemo-production-route -n $GW_PROJECT

oc create route edge httpserverdemo-staging-route \
--service=apicast-staging \
--hostname=httpserverdemo-staging.${OCP_WILDCARD_DOMAIN} \
-n $GW_PROJECT
echo -en "\n\nhttps://`oc get route httpserverdemo-staging-route --template {{.spec.host}} -n $GW_PROJECT`:443\n\n"

oc create route edge httpserverdemo-production-route \
--service=apicast-production \
--hostname=httpserverdemo-production.${OCP_WILDCARD_DOMAIN} \
-n $GW_PROJECT
echo -en "\n\nhttps://`oc get route httpserverdemo-production-route --template {{.spec.host}} -n $GW_PROJECT`:443\n\n"

# TODO
- fabric8 run
- format output based on "Accept:" header
- add liveness and health check
- mechanism to disable health check
