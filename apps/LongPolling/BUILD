COMPILE_DEPS = CORE_DEPS + KRYO + CLI + [
    "//core/store/serializers:onos-core-serializers",
    "//core/store/primitives:onos-core-primitives",
    "@httpserver//jar",
    "@JSONObject//jar",
    "@sshd_core//jar",
]

BUNDLES = [
    "@httpserver//jar",
    "@sshd_core//jar",
    "@JSONObject//jar",
    "//apps/LongPolling:onos-apps-LongPolling",
]

osgi_jar_with_tests(
    karaf_command_packages = ["org.onosproject.LongPolling"],
    test_deps = TEST_REST + ["@httpserver//jar","@JSONObject//jar"],
    deps = COMPILE_DEPS,
)

onos_app(
    category = "Fault Monitoring",
    description = "Trying Long Polling",
    included_bundles = BUNDLES,
    title = "Long Polling",
    url = "http://onosproject.org",
)
