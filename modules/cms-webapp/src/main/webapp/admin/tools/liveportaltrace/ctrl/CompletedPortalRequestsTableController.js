if (!lpt) {
    var lpt = {};
}

lpt.CompletedPortalRequestsTableController = function (tableId, automaticRefreshTimeInMillis) {
    var refreshIntervalId;
    var initialTracesToLoad = 100;
    var maxTracesToKeep = 1000;
    var loadCompletedAfterUrl;
    var loadCompletedBeforeUrl;
    var lastCompletedNumber = -1;
    var firstCompletedNumber = -1;
    var completedRequestsMap = {};
    var completedRequestsMapArray = new Array();
    var worker;
    var completedRequestsGraphController;
    var portalRequestTraceDetailController;
    var portalRequestTraceRowView;
    var table = document.getElementById(tableId);
    var tableBody = table.getElementsByTagName("tbody")[0];
    var workerThreadIsSupported = false;
    var taskInProgress = {
        loadNew: false,
        reloadAll: false
    };

    this.setWorkerThreadIsSupported = function (value) {
        workerThreadIsSupported = value;
    };

    this.setLoadCompletedAfterUrl = function (url) {
        loadCompletedAfterUrl = url;
    };

    this.setLoadCompletedBeforeUrl = function (url) {
        loadCompletedBeforeUrl = url;
    };

    this.setPortalRequestTraceRowView = function (value) {
        portalRequestTraceRowView = value;
    };

    this.setCompletedRequestsGraphController = function (ctrl) {
        completedRequestsGraphController = ctrl;
    };

    this.setPortalRequestTraceDetailController = function (ctrl) {
        portalRequestTraceDetailController = ctrl;
    };

    this.init = function () {
        if (workerThreadIsSupported) {
            worker = new Worker("liveportaltrace/request-worker.js");
            worker.onmessage = handleMessageFromWorker;
            worker.onmessage = handleMessageFromWorker;
        }

        table.addEventListener("click", function (event) {
            var clickTarget = event.target;
            var tableRow = null;
            if (clickTarget.tagName === "TD") {
                tableRow = clickTarget.parentNode;
            }
            else if (clickTarget.tagName === "TR") {
                tableRow = clickTarget;
            }

            if (tableRow != null) {
                portalRequestTraceDetailController.showPortalRequestTraceDetail(completedRequestsMap[tableRow.livePortalTraceCompletedNumber]);
            }

        });
    };

    this.loadNew = function () {
        if (workerThreadIsSupported) {
            loadNew_usingThread();
        }
        else {
            loadNew_withoutUsingThread();
        }
    };

    this.reload = function () {
        if (workerThreadIsSupported) {
            reloadAll_usingThread();
        }
        else {
            reloadAll_withoutUsingThread();
        }
    };

    this.startAutomaticRefresh = function () {
        if (workerThreadIsSupported) {
            doAutomaticRefresh_usingThread();
        }
        else {
            doAutomaticRefresh_withoutThread();
        }
    };

    function doAutomaticRefresh_withoutThread() {
        (function loop() {
            refreshIntervalId = setTimeout(function () {
                loadNew_withoutUsingThread();
                loop();
            }, automaticRefreshTimeInMillis);
        })();
    }

    function doAutomaticRefresh_usingThread() {
        (function loop() {
            refreshIntervalId = setTimeout(function () {
                loadNew_usingThread();
                loop();
            }, automaticRefreshTimeInMillis);
        })();
    }

    this.stopAutomaticRefresh = function () {
        clearInterval(refreshIntervalId);
    };

    function loadNew_withoutUsingThread() {
        if (noTasksInProgress()) {
            taskInProgress.loadNew = true;

            $.getJSON(resolveCompletedAfterUrl(lastCompletedNumber, null), function (traces) {
                insertAtTop(traces);

                taskInProgress.loadNew = false;
            });
        }
    }

    function reloadAll_withoutUsingThread() {
        if (noTasksInProgress()) {
            taskInProgress.reloadAll = true;

            jQuery.getJSON(resolveCompletedAfterUrl(lastCompletedNumber, initialTracesToLoad), function (firstTraces) {
                insertAtTop(firstTraces);
                jQuery.getJSON(resolveCompletedBeforeUrl(getFirstCompletedNumber()), function (restOfTraces) {
                    appendTraces(restOfTraces);
                    taskInProgress.reloadAll = false;
                });
            });
        }
    }

    function loadNew_usingThread() {
        if (noTasksInProgress()) {
            taskInProgress.loadNew = true;

            worker.postMessage({
                "operation": "load-new",
                "url": resolveCompletedAfterUrl(lastCompletedNumber, null)
            });
        }
    }

    function reloadAll_usingThread() {
        if (noTasksInProgress()) {
            taskInProgress.reloadAll = true;

            worker.postMessage({
                "operation": "load-all",
                "url": resolveCompletedAfterUrl(lastCompletedNumber, initialTracesToLoad)
            });
        }

    }

    function handleMessageFromWorker(event) {
        var message = event.data;

        if (message.operation === "load-new" && message.success === true) {
            var traces = jQuery.parseJSON(event.data.jsonData);
            insertAtTop(traces);
            taskInProgress.loadNew = false;
        }
        else if (message.operation === "load-new" && message.success === false) {
            console.log("Operation " + message.operation + " failed: " + message.errorMessage);
            taskInProgress.loadNew = false;
        }
        else if (message.operation === "load-all" && message.success === true) {
            insertAtTop(jQuery.parseJSON(event.data.jsonData));
            worker.postMessage({
                "operation": "load-all-remaining",
                "url": resolveCompletedBeforeUrl(getFirstCompletedNumber())
            });
        }
        else if (message.operation === "load-all" && message.success === false) {
            console.log("Operation " + message.operation + " failed: " + message.errorMessage);
            taskInProgress.reloadAll = false;
        }
        else if (message.operation === "load-all-remaining" && message.success === true) {
            appendTraces(jQuery.parseJSON(event.data.jsonData));
            taskInProgress.reloadAll = false;
        }
        else if (message.operation === "load-all-remaining" && message.success === false) {
            console.log("Operation " + message.operation + " failed: " + message.errorMessage);
            taskInProgress.reloadAll = false;
        }
    }

    function insertAtTop(traces) {
        if (traces.length > 0) {
            traces.reverse();
            for (var i = 0, length = traces.length; i < length; i++) {
                var row = traces[i];

                completedRequestsMapArray.unshift(row.portalRequestTrace.completedNumber);
                completedRequestsMap[ row.portalRequestTrace.completedNumber ] = row.portalRequestTrace;

                var firstTr = tableBody.getElementsByTagName("tr")[0];
                tableBody.insertBefore(portalRequestTraceRowView.createPortalRequestTraceTR(row), firstTr);

                if (completedRequestsMapArray.length > maxTracesToKeep) {
                    table.deleteRow(-1);
                    var completedNumberToRemove = completedRequestsMapArray.pop();
                    delete completedRequestsMap[completedNumberToRemove];
                }
            }

            lastCompletedNumber = traces[traces.length - 1].completedNumber;
            firstCompletedNumber = traces[0].completedNumber;
        }
        completedRequestsGraphController.add(traces);
    }

    function appendTraces(traces) {
        var max = traces.length;

        if (traces.length + completedRequestsMapArray.length > maxTracesToKeep) {
            max = maxTracesToKeep - completedRequestsMapArray.length;
        }

        for (var i = 0; i < max; i++) {
            completedRequestsMapArray[ completedRequestsMapArray.length ] = traces[i].portalRequestTrace.completedNumber;
            completedRequestsMap[ traces[i].portalRequestTrace.completedNumber ] = traces[i].portalRequestTrace;

            tableBody.appendChild(portalRequestTraceRowView.createPortalRequestTraceTR(traces[i]));
        }
    }

    function resolveCompletedAfterUrl(afterCompletedNumber, count) {
        if (count == null) {
            return loadCompletedAfterUrl + afterCompletedNumber;
        }
        else {
            return loadCompletedAfterUrl + afterCompletedNumber + "&count=" + count;
        }
    }

    function resolveCompletedBeforeUrl(beforeCompletedNumber) {
        return loadCompletedBeforeUrl + beforeCompletedNumber;
    }

    function getFirstCompletedNumber() {
        return firstCompletedNumber;
    }

    function noTasksInProgress() {
        return !taskInProgress.loadNew && !taskInProgress.reloadAll;
    }
};
