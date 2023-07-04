var autoRefreshIntervalId = null;

$(document).ready(function () {
  $.ajaxSetup({
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }
  });
    // Extend jQuery to support $.put() and $.delete()
    jQuery.each(["put", "delete"], function (i, method) {
      jQuery[method] = function (url, data, callback, type) {
        if (jQuery.isFunction(data)) {
          type = type || callback;
          callback = data;
          data = undefined;
        }
        return jQuery.ajax({
          url: url,
          type: method,
          dataType: type,
          data: data,
          success: callback
        });
      };
    });

    $('#inputScheduleJsonFile').on('change',function(e){
         var fileName = e.target.files[0].name;;
         $(this).next('.custom-file-label').html(fileName);
     })
    $("#refreshButton").click(function () {
      refreshTimeTable();
    });
    $("#solveButton").click(function () {
      solve();
    });
    $("#stopSolvingButton").click(function () {
      stopSolving();
    });
    $("#addMeetingSubmitButton").click(function () {
      addMeeting();
    });
    $("#updateMeetingSubmitButton").click(function () {
      updateMeeting();
    });
    $("#addTimeslotSubmitButton").click(function () {
      addTimeslot();
    });
    $("#addRoomSubmitButton").click(function () {
      addRoom();
    });
    $("#uploadSchedule").click(function () {
      uploadSchedule();
    });
    $("#getStartedButton").click(function () {
      getStartedButton();
    });
    $("#resetSchedule").click(function () {
      resetSchedule();
    });
    $("#shareButton").click(function () {
      shareSchedule();
    });
    $("#reassignMeetingsOnTimeslotButton").click(function () {
      reassignMeetingsOnTimeslot();
    });
   $("#reassignMeetingsOnRoomButton").click(function () {
     reassignMeetingsRoom();
   });
    if (getCookie("JSESSIONID") == undefined) {
        $("#greetingsDialog").modal('toggle');
    }
    else {
        refreshTimeTable();
    }
});

function resetSchedule(){
    $.post("/schedule/reset", function () {
       refreshTimeTable();
     }).fail(function (xhr, ajaxOptions, thrownError) {
      showError("Reset timetable failed.", xhr);
    });
    $('#resetDialog').modal('toggle');
}

function getStartedButton(){
    $("#greetingsDialog").modal('toggle');

    $.post("/schedule/session", function () {
       refreshTimeTable();
     }).fail(function (xhr, ajaxOptions, thrownError) {
      showError("Session failed.", xhr);
    });
}

function convertToId(str) {
  // Base64 encoding without padding to avoid XSS
  return btoa(str).replace(/=/g, "");
}

function shareSchedule(){
    $.getJSON("/schedule/share", function (timeTable) {
        const jsonString = JSON.stringify(timeTable, null, 2);
        const blob = new Blob([jsonString], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const anchorElement = document.createElement('a');
        anchorElement.href = url;
        anchorElement.download = 'schedule.json';
        anchorElement.click();
        URL.revokeObjectURL(url);
  })
}

function uploadUnsolved(e){
    var req = new XMLHttpRequest();
    req.open("POST", "/synantisi", true);
    req.setRequestHeader('Content-Type', 'application/vnd.ms-excel; charset=UTF-8');
    req.responseType = 'blob';
    const selectedFile = e.files[0];
    returnXlsx(selectedFile.name, req,selectedFile)
}

function returnXlsx(fileName, request,selectedFile) {
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
       request.send(selectedFile);
}


function refreshTimeTable() {
  $.getJSON("/schedule", function (timeTable) {
    refreshSolvingButtons(timeTable.solverStatus != null && timeTable.solverStatus !== "NOT_SOLVING");
    $("#score").text("Score: " + (timeTable.score == null ? "?" : timeTable.score));

    const scheduleByRoom = $("#scheduleByRoom");
    scheduleByRoom.children().remove();
    const scheduleByTopic = $("#scheduleByTopic");
    scheduleByTopic.children().remove();
    const scheduleByAttendees = $("#scheduleByAttendees");
    scheduleByAttendees.children().remove();
    const unassignedMeetings = $("#unassignedMeetings");
    unassignedMeetings.children().remove();

    const theadByRoom = $("<thead>").appendTo(scheduleByRoom);
    const headerRowByRoom = $("<tr>").appendTo(theadByRoom);
    headerRowByRoom.append($("<th>Timeslot</th>"));
    $.each(timeTable.roomList, (index, room) => {
      headerRowByRoom
        .append($("<th/>")
          .append($("<span/>").text(room.name))
          .append($(`<button type="button" class="ml-2 mb-1 btn btn-light btn-sm p-1"/>`)
            .append($(`<small class="fas fa-trash"/>`)).click(() => deleteRoom(room))));
    });
    const theadByTopic = $("<thead>").appendTo(scheduleByTopic);
    const headerRowByTopic = $("<tr>").appendTo(theadByTopic);
    headerRowByTopic.append($("<th>Timeslot</th>"));
    const speakerList = [...new Set(timeTable.meetingList.map(meeting => meeting.speaker))];
    $.each(speakerList, (index, topic) => {
      headerRowByTopic
        .append($("<th/>")
          .append($("<span/>").text(topic)));
    });
    const theadByStudentGroup = $("<thead>").appendTo(scheduleByAttendees);
    const headerRowByStudentGroup = $("<tr>").appendTo(theadByStudentGroup);
    headerRowByStudentGroup.append($("<th>Timeslot</th>"));
    const studentGroupList = [...new Set(timeTable.meetingList.map(meeting => meeting.attendees))];
    $.each(studentGroupList, (index, attendees) => {
      headerRowByStudentGroup
        .append($("<th/>")
          .append($("<span/>").text(attendees)));
    });

    const tbodyByRoom = $("<tbody>").appendTo(scheduleByRoom);
    const tbodyByTopic = $("<tbody>").appendTo(scheduleByTopic);
    const tbodyByStudentGroup = $("<tbody>").appendTo(scheduleByAttendees);
    $.each(timeTable.timeSlotList, (index, timeslot) => {
      const rowByRoom = $("<tr>").appendTo(tbodyByRoom);
      rowByRoom
        .append($(`<th class="align-middle"/>`)
          .append($("<span/>").text(`
                    ${timeslot.dayOfWeek.charAt(0) + timeslot.dayOfWeek.slice(1).toLowerCase()}
                    ${moment(timeslot.startTime, "HH:mm:ss").format("HH:mm")}
                    -
                    ${moment(timeslot.endTime, "HH:mm:ss").format("HH:mm")}
                `)
            .append($(`<button type="button" class="ml-2 mb-1 btn btn-light btn-sm p-1"/>`)
              .append($(`<small class="fas fa-trash"/>`)
              ).click(() => deleteTimeslot(timeslot)))));
      $.each(timeTable.roomList, (index, room) => {
        rowByRoom.append($("<td/>").prop("id", `timeslot${timeslot.id}room${room.id}`));
      });

      const rowByTopic = $("<tr>").appendTo(tbodyByTopic);
      rowByTopic
        .append($(`<th class="align-middle"/>`)
          .append($("<span/>").text(`
                    ${timeslot.dayOfWeek.charAt(0) + timeslot.dayOfWeek.slice(1).toLowerCase()}
                    ${moment(timeslot.startTime, "HH:mm:ss").format("HH:mm")}
                    -
                    ${moment(timeslot.endTime, "HH:mm:ss").format("HH:mm")}
                `)));
      $.each(speakerList, (index, topic) => {
        rowByTopic.append($("<td/>").prop("id", `timeslot${timeslot.id}teacher${convertToId(topic)}`));
      });

      const rowByStudentGroup = $("<tr>").appendTo(tbodyByStudentGroup);
      rowByStudentGroup
        .append($(`<th class="align-middle"/>`)
          .append($("<span/>").text(`
                    ${timeslot.dayOfWeek.charAt(0) + timeslot.dayOfWeek.slice(1).toLowerCase()}
                    ${moment(timeslot.startTime, "HH:mm:ss").format("HH:mm")}
                    -
                    ${moment(timeslot.endTime, "HH:mm:ss").format("HH:mm")}
                `)));
      $.each(studentGroupList, (index, attendees) => {
        rowByStudentGroup.append($("<td/>").prop("id", `timeslot${timeslot.id}studentGroup${convertToId(attendees)}`));
      });
    });

    $.each(timeTable.meetingList, (index, meeting) => {
      const color = pickColor(meeting.topic);
      const meetingElementWithoutDelete = $(`<div class="card" style="background-color: ${color}"/>`)
        .append($(`<div class="card-body p-2"/>`)
          .append($(`<h5 class="card-title mb-1"/>`).text(meeting.topic))
          .append($(`<p class="card-text ml-2 mb-1"/>`)
            .append($(`<em/>`).text(`by ${meeting.speaker}`)))
          .append($(`<small class="ml-2 mt-1 card-text text-muted align-bottom float-right"/>`).text(meeting.priority))
          .append($(`<p class="card-text ml-2"/>`).text(meeting.attendees)));
      const meetingElement = meetingElementWithoutDelete.clone();
      meetingElement.find(".card-body").prepend(
        $(`<button type="button" class="ml-2 btn btn-light btn-sm p-1 float-right"/>`)
          .append($(`<small class="fas fa-pen"/>`)
          ).click(() => editMeeting(meeting))
      );
            meetingElement.find(".card-body").prepend(
              $(`<button type="button" class="ml-2 btn btn-light btn-sm p-1 float-right"/>`)
                .append($(`<small class="fas fa-trash"/>`)
                ).click(() => deleteMeeting(meeting))
            );
      if (meeting.timeslot == null || meeting.room == null) {
        unassignedMeetings.append(meetingElement);
      } else {
        $(`#timeslot${meeting.timeslot.id}room${meeting.room.id}`).append(meetingElement);
        $(`#timeslot${meeting.timeslot.id}teacher${convertToId(meeting.speaker)}`).append(meetingElementWithoutDelete.clone());
        $(`#timeslot${meeting.timeslot.id}studentGroup${convertToId(meeting.attendees)}`).append(meetingElementWithoutDelete.clone());
      }
    });
  });
}

function solve() {
  $.post("/schedule/solve", function () {
    refreshSolvingButtons(true);
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Start solving failed.", xhr);
  });
}

function refreshSolvingButtons(solving) {
  if (solving) {
    $("#solveButton").hide();
    $("#stopSolvingButton").show();
    $("#resetSchedule").hide();
    if (autoRefreshIntervalId == null) {
      autoRefreshIntervalId = setInterval(refreshTimeTable, 2000);
    }
  } else {
    $("#resetSchedule").show();
    $("#solveButton").show();
    $("#stopSolvingButton").hide();
    if (autoRefreshIntervalId != null) {
      clearInterval(autoRefreshIntervalId);
      autoRefreshIntervalId = null;
    }
  }
}

function stopSolving() {
  $.post("/schedule/stopSolving", function () {
    refreshSolvingButtons(false);
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Stop solving failed.", xhr);
  });
}

function addMeeting() {
  var topic = $("#meeting_topic").val().trim();
  $.post("/meetings", JSON.stringify({
    "topic": topic,
    "speaker": $("#meeting_speaker").val().trim(),
    "attendees": $("#meeting_attendees").val().trim(),
    "priority": $("#meeting_priority").val().trim(),
    "sessionId": getCookie("JSESSIONID")
  }), function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Adding meeting (" + topic + ") failed.", xhr);
  });
  $('#scheduleDialog').modal('toggle');
}

function editMeeting(meeting) {
  $('#edit_meeting_topic').val(meeting.topic);
  $('#edit_meeting_speaker').val(meeting.speaker);
  $('#edit_meeting_attendees').val(meeting.attendees);
  $('#edit_meeting_priority').val(meeting.priority);
  $('#edit_meeting_id').text(meeting.id);
  $('#meetingEditDialog').modal('toggle');
}

function updateMeeting(){
    $.ajax({
        url: "/meetings/" + $("#edit_meeting_id").text().trim(),
        type: "PUT",
        data: JSON.stringify({
            "topic":   $('#edit_meeting_topic').val().trim(),
            "speaker": $("#edit_meeting_speaker").val().trim(),
            "attendees": $("#edit_meeting_attendees").val().trim(),
            "priority": $("#edit_meeting_priority").val().trim(),
            "sessionId": getCookie("JSESSIONID")
        }),
        contentType: "application/json",
        success: function(response) {
            refreshTimeTable();
        },
        error: function(xhr, status, error) {
             showError("Editing meeting (" + $('#edit_meeting_topic').val().trim() + ") failed.", xhr);
        }
    });
  $('#meetingEditDialog').modal('toggle');
}

function deleteMeeting(meeting) {
  $.delete("/meetings/" + meeting.id, function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Deleting meeting (" + meeting.name + ") failed.", xhr);
  });
}

function addTimeslot() {
  $.post("/timeslots", JSON.stringify({
    "dayOfWeek": $("#timeslot_dayOfWeek").val().trim().toUpperCase(),
    "startTime": $("#timeslot_startTime").val().trim(),
    "endTime": $("#timeslot_endTime").val().trim(),
    "sessionId": getCookie("JSESSIONID")
  }), function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Adding timeslot failed.", xhr);
  });
  $('#timeslotDialog').modal('toggle');
}

function reassignMeetingsOnTimeslot(){
  var timeslotId = $('#reassignMeetingsTimeslotId').text().trim();

  $.delete("/schedule/timeslot/" + timeslotId, function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Deleting timeslot failed.", xhr);
  });

  $('#timeSlotDeleteDialog').modal('toggle');
}

function deleteTimeslot(timeslot) {
  $.getJSON("/schedule/timeslot/" + timeslot.id, function (meetings) {
    var topics = meetings.map(meeting => meeting.topic);
    var ids = meetings.map(meeting => meeting.id);
    $('#reassignMeetings').text(topics.join(", "));
    $('#reassignMeetingsTimeslotId').text(timeslot.id).hide();
  });
  $('#timeSlotDeleteDialog').modal('toggle');
}

function deleteRoom(room) {
 $.getJSON("/schedule/room/" + room.id, function (meetings) {
    var topics = meetings.map(meeting => meeting.topic);
    var ids = meetings.map(meeting => meeting.id);
    $('#reassignMeetingsRoom').text(topics.join(", "));
    $('#reassignMeetingsRoomId').text(room.id).hide();
  });
  $('#roomDeleteDialog').modal('toggle');
}

function reassignMeetingsRoom(){
  var roomId = $('#reassignMeetingsRoomId').text().trim();

  $.delete("/schedule/room/" + roomId, function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Deleting room failed.", xhr);
  });

  $('#roomDeleteDialog').modal('toggle');
}

function addRoom() {
  var name = $("#room_name").val().trim();
  $.post("/rooms", JSON.stringify({
    "name": name,
    "sessionId": getCookie("JSESSIONID")
  }), function () {
    refreshTimeTable();
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Adding room (" + name + ") failed.", xhr);
  });
  $("#roomDialog").modal('toggle');
}

function uploadSchedule(){
  const fileInput = document.getElementById('inputScheduleJsonFile');
    const file = fileInput.files[0];

    if (!file) {
      console.log('No file selected.');
      return;
    }

    const reader = new FileReader();
    reader.onload = function(event) {
      const jsonData = event.target.result;
      $.post("/schedule/upload", JSON.stringify(jsonData)
      , function () {
          refreshTimeTable();
        }).fail(function (xhr, ajaxOptions, thrownError) {
          showError("Meeting upload (" + fileInput + ") failed.", xhr);
        });
    };
    reader.readAsText(file);
    $('#uploadDialog').modal('toggle');
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

const SEQUENCE_1 = [0x8AE234, 0xAD7FA8, 0xFCE94F, 0x729FCF, 0xE9B96E];
const SEQUENCE_2 = [0x73D216, 0x75507B, 0xEDD400, 0x3465A4, 0xC17D11];

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

function getCookie(name) {
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length == 2) return parts.pop().split(";").shift();
}
