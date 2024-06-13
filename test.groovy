pipeline {
    agent any
    stages {
        stage('Health Check') {
            steps {
                script {
                    // 假设我们有一个函数获取 Health Check 的结果
                    def healthChecks = getHealthCheckResults()

                    // 将 Health Check 结果分为成功和失败两部分
                    def failedChecks = healthChecks.findAll { it.status == 'Failed' }
                    def successfulChecks = healthChecks.findAll { it.status == 'Success' }

                    // 生成 HTML 内容
                    def htmlContent = generateHtmlContent(failedChecks, successfulChecks)

                    // 发送邮件
                    emailext subject: "Health Check Report",
                             body: htmlContent,
                             mimeType: 'text/html',
                             to: 'recipient@example.com'
                }
            }
        }
    }
}

def getHealthCheckResults() {
    return [
        [deployment: 'Deployment A', url: 'http://example.com/healthcheckA', status: 'Failed', responseCode: 500, responseBody: 'Internal Server Error', acceptCode: 200],
        [deployment: 'Deployment B', url: 'http://example.com/healthcheckB', status: 'Success', responseCode: 200, responseBody: 'OK', acceptCode: 200]
        // 这里可以添加更多的 Health Check 结果
    ]
}

def generateHtmlContent(failedChecks, successfulChecks) {
    def failedRows = failedChecks.collect { check ->
        """
        <tr>
            <td>${check.deployment}</td>
            <td><a href="${check.url}">${check.url}</a></td>
            <td class="status-failure">Failed</td>
            <td>${check.responseCode}</td>
            <td>${check.responseBody}</td>
            <td>${check.acceptCode}</td>
        </tr>
        """
    }.join('\n')

    def successfulRows = successfulChecks.collect { check ->
        """
        <tr>
            <td>${check.deployment}</td>
            <td><a href="${check.url}">${check.url}</a></td>
            <td class="status-success">Success</td>
            <td>${check.responseCode}</td>
            <td>${check.responseBody}</td>
            <td>${check.acceptCode}</td>
        </tr>
        """
    }.join('\n')

    return """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            table {
                width: 100%;
                border-collapse: collapse;
            }
            th, td {
                border: 1px solid #dddddd;
                text-align: left;
                padding: 8px;
            }
            th {
                background-color: #f2f2f2;
            }
            .status-success {
                color: green;
            }
            .status-failure {
                color: red;
            }
        </style>
        <title>Health Check Report</title>
    </head>
    <body>
        <h1>Health Check Report</h1>

        <h2>Failed Health Checks</h2>
        <table>
            <thead>
                <tr>
                    <th>Deployment Name</th>
                    <th>Check URL</th>
                    <th>Status</th>
                    <th>Response Code</th>
                    <th>Response Body</th>
                    <th>Accept Code</th>
                </tr>
            </thead>
            <tbody>
                ${failedRows}
            </tbody>
        </table>

        <h2>Successful Health Checks</h2>
        <table>
            <thead>
                <tr>
                    <th>Deployment Name</th>
                    <th>Check URL</th>
                    <th>Status</th>
                    <th>Response Code</th>
                    <th>Response Body</th>
                    <th>Accept Code</th>
                </tr>
            </thead>
            <tbody>
                ${successfulRows}
            </tbody>
        </table>
    </body>
    </html>
    """
}
