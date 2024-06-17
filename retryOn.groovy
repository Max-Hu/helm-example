def updateRetryOn(filePath) {
    // 读取 YAML 文件
    def yamlContent = readYaml file: filePath

    // 遍历 YAML 内容，找到 VirtualService 的部分并修改 retryOn 的值
    yamlContent.each { resource ->
        if (resource.kind == 'VirtualService') {
            resource.spec.http.each { http ->
                if (http.retries && http.retries.retryOn) {
                    // 将列表形式转换为逗号分隔的字符串，并处理布尔值
                    def retryOnList = http.retries.retryOn.collect { entry ->
                        entry.find { key, value -> value == true }?.key
                    }.findAll { it != null }.collect { camelToKebabCase(it) }.join(", ")
                    http.retries.retryOn = retryOnList
                }
            }
        }
    }

    // 将修改后的 YAML 内容写回文件
    writeYaml file: filePath, data: yamlContent
}

// 辅助方法：将驼峰命名转换为减号连接
def camelToKebabCase(String camelCase) {
    camelCase.replaceAll(/([a-z])([A-Z])/, '$1-$2').toLowerCase()
}

// 示例调用
node {
    stage('Update VirtualService YAML') {
        // 替换为实际的文件路径
        def filePath = 'path/to/your/helm-generated.yaml'
        updateRetryOn(filePath)
    }
}
