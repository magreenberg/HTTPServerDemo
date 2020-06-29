s2i build . registry.redhat.io/redhat-openjdk-18/openjdk18-openshift:latest quay.io/mgreenbe/httpserverdemo:1
docker push quay.io/mgreenbe/httpserverdemo:1
# make repo public
oc process -f httpserverdemo.yml -p HTTP_PORT=8088 | oc create -f -


# Alternative build
oc new-project httpserverdemo
oc get is -n openshift | grep jdk
oc new-app openshift/redhat-openjdk18-openshift:1.5~https://github.com/magreenberg/HTTPServerDemo.git


# TODO
- fabric8 run
- format output based on "Accept:" header
- add liveness and health check
- mechanism to disable health check
