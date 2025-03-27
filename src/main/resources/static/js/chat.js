/**
 * WebSocket chat functionality
 */

let ws;
let messageInput;
let sendButton;
let connectButton;
let nameInput;
let connectionStatus;
let errorMessage;

// Initialize when DOM is loaded
document.addEventListener("DOMContentLoaded", function() {
    // Get DOM elements
    messageInput = document.getElementById("message");
    sendButton = document.getElementById("sendButton");
    connectButton = document.getElementById("connectButton");
    nameInput = document.getElementById("name");
    connectionStatus = document.getElementById("connectionStatus");
    errorMessage = document.getElementById("errorMessage");
    
    // We don't initialize the user counter when the page loads
    // It will only be shown when the first USER_COUNT message is received

    // Enable pressing Enter to send a message
    messageInput.addEventListener("keypress", function(event) {
        if (event.key === "Enter") {
            event.preventDefault();
            sendToGroupChat();
        }
    });

    // Enable pressing Enter to connect
    nameInput.addEventListener("keypress", function(event) {
        if (event.key === "Enter") {
            event.preventDefault();
            connect();
        }
    });

    // Listen for i18n updates to update connection status
    document.addEventListener('i18n:updated', function() {
        if (connectionStatus) {
            updateConnectionStatus(connectionStatus.className.split(" ")[1]);
        }
    });
});

/**
 * Display an error message for a short time
 */
function showError(messageKey, ...args) {
    const translatedMessage = t(messageKey, ...args);
    errorMessage.textContent = translatedMessage;
    errorMessage.style.display = "block";
    setTimeout(() => {
        errorMessage.style.display = "none";
    }, 5000);
}

/**
 * Update the connection status display
 */
function updateConnectionStatus(status) {
    connectionStatus.className = "status " + status;
    switch(status) {
        case "connected":
            connectionStatus.textContent = t("ui.connection.connected");
            break;
        case "disconnected":
            connectionStatus.textContent = t("ui.connection.disconnected");
            break;
        case "connecting":
            connectionStatus.textContent = t("ui.connection.connecting");
            break;
    }
}

/**
 * Update online users counter with proper pluralization
 */
function updateOnlineUsers(count) {
    const onlineUsersElement = document.getElementById("onlineUsers");
    if (onlineUsersElement) {
        // Show the element if it's hidden (first time)
        if (onlineUsersElement.style.display === 'none') {
            onlineUsersElement.style.display = 'block';
        }
        
        // Print debug info to console in development mode
        if (window.i18n && window.i18n.developmentMode) {
            console.log("Updating online users count:", count);
            console.log("Current locale:", window.i18n.currentLocale);
            console.log("Available plural forms for 'users.online':", {
                "zero": window.i18n.messages["users.online.zero"],
                "one": window.i18n.messages["users.online.one"],
                "other": window.i18n.messages["users.online.other"]
            });
            console.log("Selected plural form:", window.i18n.getPluralForm(count));
        }
        
        onlineUsersElement.textContent = plural("users.online", count);
    }
}

/**
 * Connect to the WebSocket server
 */
function connect() {
    const name = nameInput.value.trim();
    if (!name) {
        showError("ui.error.name.required");
        return;
    }

    updateConnectionStatus("connecting");
    
    try {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        ws = new WebSocket(`${protocol}//${window.location.host}/chat`);
        
        ws.onopen = function() {
            updateConnectionStatus("connected");
            connectButton.disabled = true;
            nameInput.disabled = true;
            messageInput.disabled = false;
            sendButton.disabled = false;
            messageInput.focus();
        };
        
        ws.onmessage = function(e) {
            printMessage(e.data);
        };
        
        ws.onclose = function() {
            updateConnectionStatus("disconnected");
            connectButton.disabled = false;
            nameInput.disabled = false;
            messageInput.disabled = true;
            sendButton.disabled = true;
            
            // Hide the user counter when the connection is closed
            const onlineUsersElement = document.getElementById("onlineUsers");
            if (onlineUsersElement) {
                onlineUsersElement.style.display = 'none';
            }
        };
        
        ws.onerror = function(e) {
            showError("ui.error.websocket");
            updateConnectionStatus("disconnected");
            console.error("WebSocket error:", e);
        };
    } catch (error) {
        showError("ui.error.connection.failed", error.message);
        updateConnectionStatus("disconnected");
        console.error("Connection error:", error);
    }
}

/**
 * Format ISO timestamp to readable time using Intl API
 */
function formatTimestamp(isoString) {
    if (!isoString) return '';
    
    try {
        const date = new Date(isoString);
        return formatDate(date);
    } catch (e) {
        console.error("Error formatting timestamp:", e);
        return '';
    }
}

/**
 * Display a message in the chat
 */
function printMessage(data) {
    try {
        const messages = document.getElementById("messages");
        const messageData = JSON.parse(data);
        
        const newMessage = document.createElement("div");
        newMessage.className = "message";
        
        // Handle different message types
        switch(messageData.type) {
            case "ERROR":
                newMessage.classList.add("error");
                break;
            case "JOIN":
                newMessage.classList.add("join");
                break;
            case "LEAVE":
                newMessage.classList.add("leave");
                break;
            case "USER_COUNT":
                // Update online users counter and skip adding this message to chat
                updateOnlineUsers(parseInt(messageData.message, 10));
                return;
            case "CHAT":
            default:
                // Regular chat message
                break;
        }
        
        const sender = document.createElement("div");
        sender.className = "sender";
        sender.textContent = messageData.name;
        
        const content = document.createElement("div");
        content.className = "content";
        content.textContent = messageData.message;
        
        const timestamp = document.createElement("div");
        timestamp.className = "timestamp";
        timestamp.textContent = formatTimestamp(messageData.timestamp);
        
        newMessage.appendChild(sender);
        newMessage.appendChild(content);
        newMessage.appendChild(timestamp);
        messages.appendChild(newMessage);
        
        // Auto-scroll to bottom
        messages.scrollTop = messages.scrollHeight;
    } catch (error) {
        console.error("Error parsing message:", error);
        showError("ui.error.display.message");
    }
}

/**
 * Send a message to the group chat
 */
function sendToGroupChat() {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        showError("ui.error.not.connected");
        return;
    }
    
    const messageText = messageInput.value.trim();
    if (!messageText) {
        showError("ui.error.message.required");
        return;
    }
    
    const name = nameInput.value;
    const messageObject = {
        name: name,
        message: messageText,
        type: "CHAT"
    };
    
    try {
        ws.send(JSON.stringify(messageObject));
        messageInput.value = "";
        messageInput.focus();
    } catch (error) {
        showError("ui.error.send.failed", error.message);
        console.error("Send error:", error);
    }
}

// Close WebSocket connection when window is closed
window.addEventListener("beforeunload", function() {
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.close();
    }
});
