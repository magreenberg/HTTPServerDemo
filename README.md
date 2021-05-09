```bash
REGISTRY="$(oc get route/default-route -n openshift-image-registry -o=jsonpath='{.spec.host}')/openshift"

docker login -u unused -p $(oc whoami -t) ${REGISTRY}

oc new-project httpserverdemo

s2i build . registry.redhat.io/redhat-openjdk-18/openjdk18-openshift ${REGISTRY}/httpserverdemo:1
docker push ${REGISTRY}/httpserverdemo:1
# make repo public
oc process -f httpserverdemo.yml -p HTTP_PORT=8080 | oc create -f -
```


# Alternative build
oc get is -n openshift | grep jdk
oc new-app openshift/redhat-openjdk18-openshift:1.8~https://github.com/magreenberg/HTTPServerDemo.git


# TODO
- fabric8 run
- format output based on "Accept:" header
- add liveness and health check
- mechanism to disable health check
