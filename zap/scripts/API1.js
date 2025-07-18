// Java class mapping
var HttpSender = Java.type("org.parosproxy.paros.network.HttpSender");
var HttpMessage = Java.type("org.parosproxy.paros.network.HttpMessage");
var URI = Java.type("org.apache.commons.httpclient.URI");
var HttpHeader = Java.type("org.parosproxy.paros.network.HttpHeader");
var FileWriter = Java.type("java.io.FileWriter");
var BufferedWriter = Java.type("java.io.BufferedWriter");
var SimpleDateFormat = Java.type("java.text.SimpleDateFormat");
var Date = Java.type("java.util.Date");
// Gán thông tin đăng nhập API
var loginUrl = "http://localhost:8080/auth/log-in";
var loginBody = '{"username":"orabSihc","password":"orab1234567"}';
var token = null;

// === Thông tin log ===
var logFile = "C:/Users/Admin/Desktop/report.txt";
var writer = null;
var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

// === Khởi tạo log file ===
function initLog() {
  writer = new BufferedWriter(new FileWriter(logFile, true));
  var now = formatter.format(new Date());
  writer.write("\n=== BOLA Scan Log - " + now + " ===\n");
  writer.flush();
}

function cleanup() {
  if (writer != null) {
    writer.close();
  }
}

function getJwtToken() {
  try {
    var loginMsg = new HttpMessage(new URI(loginUrl, false));
    loginMsg.getRequestHeader().setMethod("POST");
    loginMsg
      .getRequestHeader()
      .setHeader(HttpHeader.CONTENT_TYPE, "application/json");
    loginMsg.setRequestBody(loginBody);
    loginMsg.getRequestHeader().setContentLength(loginBody.length);

    var sender = new HttpSender(HttpSender.MANUAL_REQUEST_INITIATOR);
    sender.sendAndReceive(loginMsg, true);

    var response = loginMsg.getResponseBody().toString();
    var parsed = JSON.parse(response);

    return parsed.token;
  } catch (e) {
    print("[ERROR] Cannot get token: " + e);
    return null;
  }
}

function sendingRequest(msg, initiator, helper) {
  var CURRENT_ID = 6;
  //   var TARGET_IDS = [1, 3, 4, 5, 2];
  var TARGET_IDS = [1, 3, 4, 5, 2, 7, 8, 9, 10];
  var uri = msg.getRequestHeader().getURI().toString();

  if (uri.contains("/user/getById/" + CURRENT_ID)) {
    // Gọi initLog nếu chưa mở file log
    if (writer == null) {
      initLog();
    }

    if (token == null) {
      token = getJwtToken();
      if (!token) {
        // print("[ERROR] Could not get token");
        return;
      } else {
        // print("[INFO] Token obtained: " + token);
      }
    }

    for (var i = 0; i < TARGET_IDS.length; i++) {
      var TARGET_ID = TARGET_IDS[i];
      var newUri = uri.replace("/" + CURRENT_ID, "/" + TARGET_ID);

      try {
        var forgedMsg = new HttpMessage(new URI(newUri, false));
        forgedMsg.getRequestHeader().setMethod("GET");
        forgedMsg
          .getRequestHeader()
          .setHeader("Authorization", "Bearer " + token);

        var sender = new HttpSender(HttpSender.MANUAL_REQUEST_INITIATOR);
        sender.sendAndReceive(forgedMsg, true);

        var status = forgedMsg.getResponseHeader().getStatusCode();
        print("[*] ID " + TARGET_ID + " → status: " + status);
        var timestamp = formatter.format(new Date());
        if (status === 200) {
          var vuln =
            "[" + timestamp + "] [!!] BOLA vulnerability found at: " + newUri;
          print(vuln);
          writer.write(vuln + "\n");
        }
      } catch (e) {
        print("Gửi request với ID " + TARGET_ID + ": " + e);
      }
    }
  }
  cleanup();
}

function responseReceived(msg, initiator, helper) {
  // Không xử lý phản hồi chính gốc
}
