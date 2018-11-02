package main_test

import (
    . "github.com/onsi/ginkgo"
    . "github.com/onsi/gomega"
    "github.com/onsi/gomega/gexec"
    "io/ioutil"
    "net/http"
    "os"
    "os/exec"
    "strings"
    "time"
)

var _ = Describe("MetricRegistrarExamples", func() {
    var session *gexec.Session
    BeforeEach(func() {
        session = startApp()
    })

    AfterEach(func() {
        session.Terminate()
        Eventually(session.Exited).Should(BeClosed())
    })

    Context("simple endpoint", func() {
        It("returns OK", func() {
            resp, err := http.Get("http://localhost:8080/simple")
            Expect(err).ToNot(HaveOccurred())

            expectOk(resp)
        })
    })

    Context("high latency endpoint", func() {
        It("returns OK slowly", func() {
            start := time.Now()
            resp, err := http.Get("http://localhost:8080/high_latency")
            Expect(err).ToNot(HaveOccurred())
            duration := time.Now().Sub(start)

            expectOk(resp)
            Expect(duration).To(BeNumerically(">", 2*time.Second))
        })
    })

    Context("custom metric endpoint", func() {
        It("increments the custom metric", func() {
            resp, err := http.Get("http://localhost:8080/custom_metric")
            Expect(err).ToNot(HaveOccurred())

            expectOk(resp)

            promResp, err := http.Get("http://localhost:8080/metrics")
            Expect(err).ToNot(HaveOccurred())

            bodyBytes, err := ioutil.ReadAll(promResp.Body)
            Expect(err).ToNot(HaveOccurred())

            lines := strings.Split(string(bodyBytes), "\n")
            Expect(lines).To(ContainElement("custom 1"))
        })
    })
})

func expectOk(resp *http.Response) {
    Expect(resp.StatusCode).To(Equal(http.StatusOK))

    bodyBytes, err := ioutil.ReadAll(resp.Body)
    Expect(err).ToNot(HaveOccurred())
    Expect(bodyBytes).To(BeEquivalentTo("{}"))
}

func startApp() *gexec.Session {
    commandPath, err := gexec.Build(os.Getenv("GOPATH")+"/src/metric_registrar_examples", "-race")
    Expect(err).ToNot(HaveOccurred())

    command := exec.Command(commandPath)
    command.Env = []string{
        "PORT=8080",
    }

    checkAppNotRunning()

    session, err := gexec.Start(command, GinkgoWriter, GinkgoWriter)
    Expect(err).ToNot(HaveOccurred())

    Eventually(func() int {
        resp, err := http.Get("http://localhost:8080/metrics")
        if err != nil {
            return 0
        }

        return resp.StatusCode
    }).Should(Equal(http.StatusOK))

    return session
}

func checkAppNotRunning() {
    _, err := http.Get("http://localhost:8080/metrics")
    Expect(err).To(HaveOccurred())
}
