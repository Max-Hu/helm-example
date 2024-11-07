def updateJiraStatus(targetStatus) {
    def currentStatus = getCurrentStatus() // 获取当前状态
    
    // 特殊处理：如果起始状态是 `Backlog`，必须先经过 `Ready for Development` 和 `In Development`
    if (currentStatus == "Backlog") {
        transitionToStatus(currentStatus, "Ready for Development")
        currentStatus = "Ready for Development"
        transitionToStatus(currentStatus, "In Development")
        currentStatus = "In Development"
    }

    // 如果目标状态是 `In Development`，则直接返回
    if (targetStatus == "In Development" && currentStatus == "In Development") {
        echo "Already in target status: ${targetStatus}"
        return
    }

    // 获取到目标状态的路径并逐步更新状态
    def transitionPath = getTransitionPath(currentStatus, targetStatus)
    if (transitionPath == null) {
        error "Cannot reach target status ${targetStatus} from ${currentStatus} due to invalid transition rules."
    }
    
    for (status in transitionPath) {
        if (currentStatus != status) {
            transitionToStatus(currentStatus, status) // 仅在必要时进行 API 请求
            currentStatus = status // 更新本地状态
        }
    }
}


##############################

def updateJiraStatus(targetStatus) {
    def currentStatus = getCurrentStatus() // Initial API call to get current status
    if (currentStatus == targetStatus) {
        echo "Already in target status: ${targetStatus}"
        return
    }
    
    def transitionPath = getTransitionPath(currentStatus, targetStatus)
    if (transitionPath == null) {
        error "Cannot reach target status ${targetStatus} from ${currentStatus} due to invalid transition rules."
    }
    
    for (status in transitionPath) {
        if (currentStatus != status) {
            transitionToStatus(currentStatus, status) // Make API call only when necessary
            currentStatus = status // Update local state to avoid unnecessary API calls
        }
    }
}

def getTransitionPath(currentStatus, targetStatus) {
    def transitionMap = [
        "Backlog"             : ["Ready for Development"],
        "Ready for Development": ["In Development"],
        "In Development"      : ["Dev Done"],
        "Dev Done"            : ["In SIT"],
        "In SIT"              : ["In UAT"],
        "In UAT"              : ["Ready for Release"],
        "Ready for Release"   : ["Done"]
    ]
    
    def path = []
    while (currentStatus != targetStatus) {
        def nextStates = transitionMap[currentStatus]
        if (nextStates == null || nextStates.isEmpty()) {
            return null
        }
        currentStatus = nextStates[0]
        path.add(currentStatus)
    }
    return path
}

def getCurrentStatus() {
    // API call to fetch the current status (simulate with a placeholder)
    return "Backlog"
}

def transitionToStatus(currentStatus, nextStatus) {
    // Simulate API call to transition status
    echo "Transitioning from ${currentStatus} to ${nextStatus}"
    // Actual API request logic here
}
