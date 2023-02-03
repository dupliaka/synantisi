var autoRefreshIntervalId = null;
const dateTimeFormatter = JSJoda.DateTimeFormatter.ofPattern('HH:mm')

$(document).ready(function () {
  $.ajaxSetup({
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }
  });

  $("#refreshButton").click(function () {
    refreshTimeTable();
  });
  $("#solveButton").click(function () {
    solve();
  });
  $("#stopSolvingButton").click(function () {
    stopSolving();
  });

  $("#unsolved").click(function () {
       uploadUnsolved();
  });

});

function convertToId(str) {
  // Base64 encoding without padding to avoid XSS
  return btoa(str).replace(/=/g, "");
}

function uploadUnsolved(){

    alert("af");

}

function solve() {
    var request = new XMLHttpRequest();
    request.open('GET', "/synantisi");
    request.setRequestHeader('Content-Type', 'application/vnd.ms-excel; charset=UTF-8');
    request.responseType = 'blob';
    returnXlsx("solved.xlsx", request)
}

function returnXlsx(fileName, request) {
    request.onload = function(e) {
        if (this.status === 200) {
            var blob = this.response;
            if(window.navigator.msSaveOrOpenBlob) {
                window.navigator.msSaveBlob(blob, fileName);
            }
            else{
                var downloadLink = window.document.createElement('a');
                var contentTypeHeader = request.getResponseHeader("Content-Type");
                downloadLink.href = window.URL.createObjectURL(new Blob([blob], { type: contentTypeHeader }));
                downloadLink.download = fileName;
                document.body.appendChild(downloadLink);
                downloadLink.click();
                document.body.removeChild(downloadLink);
               }
           }
       };
       request.send();
}

function refreshSolvingButtons(solving) {
  if (solving) {
    $("#solveButton").hide();
    $("#stopSolvingButton").show();
    if (autoRefreshIntervalId == null) {
      autoRefreshIntervalId = setInterval(refreshTimeTable, 2000);
    }
  } else {
    $("#solveButton").show();
    $("#stopSolvingButton").hide();
    if (autoRefreshIntervalId != null) {
      clearInterval(autoRefreshIntervalId);
      autoRefreshIntervalId = null;
    }
  }
}

function stopSolving() {
  $.post("/timeTable/stopSolving", function () {
    refreshSolvingButtons(false);
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Stop solving failed.", xhr);
  });
}

function addLesson() {
  var subject = $("#lesson_subject").val().trim();
  $.post("/lessons", JSON.stringify({
    "subject": subject,
    "teacher": $("#lesson_teacher").val().trim(),
    "studentGroup": $("#lesson_studentGroup").val().trim()
  }), function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Adding lesson (" + subject + ") failed.", xhr);
  });
  $('#lessonDialog').modal('toggle');
}

function deleteLesson(lesson) {
  $.delete("/lessons/" + lesson.id, function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Deleting lesson (" + lesson.name + ") failed.", xhr);
  });
}

function addTimeslot() {
  $.post("/timeslots", JSON.stringify({
    "dayOfWeek": $("#timeslot_dayOfWeek").val().trim().toUpperCase(),
    "startTime": $("#timeslot_startTime").val().trim(),
    "endTime": $("#timeslot_endTime").val().trim()
  }), function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Adding timeslot failed.", xhr);
  });
  $('#timeslotDialog').modal('toggle');
}

function deleteTimeslot(timeslot) {
  $.delete("/timeslots/" + timeslot.id, function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Deleting timeslot (" + timeslot.name + ") failed.", xhr);
  });
}

function addRoom() {
  var name = $("#room_name").val().trim();
  $.post("/rooms", JSON.stringify({
    "name": name
  }), function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Adding room (" + name + ") failed.", xhr);
  });
  $("#roomDialog").modal('toggle');
}

function deleteRoom(room) {
  $.delete("/rooms/" + room.id, function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Deleting room (" + room.name + ") failed.", xhr);
  });
}

function showError(title, xhr) {
  const serverErrorMessage = !xhr.responseJSON ? `${xhr.status}: ${xhr.statusText}` : xhr.responseJSON.message;
  console.error(title + "\n" + serverErrorMessage);
  const notification = $(`<div class="toast" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 30rem"/>`)
    .append($(`<div class="toast-header bg-danger">
                 <strong class="mr-auto text-dark">Error</strong>
                 <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                   <span aria-hidden="true">&times;</span>
                 </button>
               </div>`))
    .append($(`<div class="toast-body"/>`)
      .append($(`<p/>`).text(title))
      .append($(`<pre/>`)
        .append($(`<code/>`).text(serverErrorMessage))
      )
    );
  $("#notificationPanel").append(notification);
  notification.toast({delay: 30000});
  notification.toast('show');
}

// ****************************************************************************
// TangoColorFactory
// ****************************************************************************

const SEQUENCE_1 = [0x8AE234, 0xFCE94F, 0x729FCF, 0xE9B96E, 0xAD7FA8];
const SEQUENCE_2 = [0x73D216, 0xEDD400, 0x3465A4, 0xC17D11, 0x75507B];

var colorMap = new Map;
var nextColorCount = 0;

function pickColor(object) {
  let color = colorMap[object];
  if (color !== undefined) {
    return color;
  }
  color = nextColor();
  colorMap[object] = color;
  return color;
}

function nextColor() {
  let color;
  let colorIndex = nextColorCount % SEQUENCE_1.length;
  let shadeIndex = Math.floor(nextColorCount / SEQUENCE_1.length);
  if (shadeIndex === 0) {
    color = SEQUENCE_1[colorIndex];
  } else if (shadeIndex === 1) {
    color = SEQUENCE_2[colorIndex];
  } else {
    shadeIndex -= 3;
    let floorColor = SEQUENCE_2[colorIndex];
    let ceilColor = SEQUENCE_1[colorIndex];
    let base = Math.floor((shadeIndex / 2) + 1);
    let divisor = 2;
    while (base >= divisor) {
      divisor *= 2;
    }
    base = (base * 2) - divisor + 1;
    let shadePercentage = base / divisor;
    color = buildPercentageColor(floorColor, ceilColor, shadePercentage);
  }
  nextColorCount++;
  return "#" + color.toString(16);
}

function buildPercentageColor(floorColor, ceilColor, shadePercentage) {
  let red = (floorColor & 0xFF0000) + Math.floor(shadePercentage * ((ceilColor & 0xFF0000) - (floorColor & 0xFF0000))) & 0xFF0000;
  let green = (floorColor & 0x00FF00) + Math.floor(shadePercentage * ((ceilColor & 0x00FF00) - (floorColor & 0x00FF00))) & 0x00FF00;
  let blue = (floorColor & 0x0000FF) + Math.floor(shadePercentage * ((ceilColor & 0x0000FF) - (floorColor & 0x0000FF))) & 0x0000FF;
  return red | green | blue;
}
