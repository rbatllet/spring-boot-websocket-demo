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
let messageHistorySeparator = null;
let newMessagesSeparator = null;

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
    
    // Add input event listener to enable/disable send button based on content
    messageInput.addEventListener("input", function() {
        if (messageInput.disabled) return;
        
        // Enable button only if there's at least one non-whitespace character
        if (messageInput.value.trim().length > 0) {
            sendButton.disabled = false;
        } else {
            sendButton.disabled = true;
        }
    });

    // Enable pressing Enter to connect
    nameInput.addEventListener("keypress", function(event) {
        if (event.key === "Enter") {
            event.preventDefault();
            connect();
        }
    });
    
    // Add input event listener to enable/disable connect button based on name content
    nameInput.addEventListener("input", function() {
        // Enable button only if there's at least one non-whitespace character
        if (nameInput.value.trim().length > 0) {
            connectButton.disabled = false;
        } else {
            connectButton.disabled = true;
        }
    });
    
    // Initial check for name input to set connect button state
    connectButton.disabled = nameInput.value.trim().length === 0;
    
    // Toggle formatting options visibility
    const formattingToggle = document.getElementById("formattingToggle");
    const formattingOptions = document.getElementById("formattingOptions");
    if (formattingToggle && formattingOptions) {
        formattingToggle.addEventListener("click", function() {
            if (formattingOptions.style.display === "none") {
                formattingOptions.style.display = "block";
                formattingToggle.textContent = t("ui.formatting.hide");
            } else {
                formattingOptions.style.display = "none";
                formattingToggle.textContent = t("ui.formatting.toggle");
            }
        });
    }

    // Listen for i18n updates to update connection status and button text
    document.addEventListener('i18n:updated', function() {
        if (connectionStatus) {
            updateConnectionStatus(connectionStatus.className.split(" ")[1]);
        }
        
        // Update connect/disconnect button text based on connection state
        if (ws && ws.readyState === WebSocket.OPEN) {
            connectButton.textContent = t("ui.button.disconnect");
            connectButton.setAttribute("data-i18n", "ui.button.disconnect");
        } else {
            connectButton.textContent = t("ui.button.connect");
            connectButton.setAttribute("data-i18n", "ui.button.connect");
        }
        
        // Update online users count with current language
        const onlineUsersElement = document.getElementById("onlineUsers");
        if (onlineUsersElement && onlineUsersElement.dataset.count) {
            const count = parseInt(onlineUsersElement.dataset.count, 10);
            updateOnlineUsersText(count);
        }
    });
});

/**
 * Log an error to the console without showing it in the UI
 */
function showError(messageKey, ...args) {
    // Only log to console, don't show in UI
    console.error(t(messageKey, ...args));
    
    // The error message element is no longer used
    // if (!errorMessage) return;
    // errorMessage.textContent = t(messageKey, ...args);
    // errorMessage.style.display = "block";
    // setTimeout(function() {
    //     errorMessage.style.display = "none";
    // }, 5000);
}

/**
 * Update the connection status display
 */
function updateConnectionStatus(status) {
    if (!connectionStatus) return;
    
    // Update the status class
    connectionStatus.className = "status " + status;
    
    // Update the status text based on the class
    switch (status) {
        case "connected":
            connectionStatus.textContent = t("ui.connection.connected");
            break;
        case "connecting":
            connectionStatus.textContent = t("ui.connection.connecting");
            break;
        default:
            connectionStatus.textContent = t("ui.connection.disconnected");
    }
}

/**
 * Update online users counter with proper pluralization
 */
function updateOnlineUsers(count) {
    const onlineUsersElement = document.getElementById("onlineUsers");
    
    if (onlineUsersElement) {
        // Store the count as a data attribute for language changes
        onlineUsersElement.dataset.count = count;
        
        // Update the text with the current language
        updateOnlineUsersText(count);
    }
}

/**
 * Update online users text with current language
 */
function updateOnlineUsersText(count) {
    const onlineUsersElement = document.getElementById("onlineUsers");
    
    if (onlineUsersElement) {
        // Use the appropriate translation based on count
        let key;
        if (count === 0) {
            key = "users.online.zero";
        } else if (count === 1) {
            key = "users.online.one";
        } else {
            key = "users.online.other";
        }
        
        // Get the translation and replace the placeholder
        const translation = t(key, count);
        onlineUsersElement.textContent = translation;
        
        // Log for debugging
        console.log(`Updating users count: ${count}, using key: ${key}, result: ${translation}`);
    }
}

/**
 * Fetch chat message history from the server
 */
function fetchMessageHistory() {
    fetch('/api/chat/messages/chat')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(messages => {
            // Display message history (newest messages first, so reverse to show oldest first)
            const messagesContainer = document.getElementById("messages");
            
            // Only show history and separators if there are messages
            if (messages && messages.length > 0) {
                // Add a history separator
                const separator = document.createElement("div");
                separator.className = "history-separator";
                const separatorSpan = document.createElement("span");
                const historyText = t("ui.message.history");
                console.log("History separator text:", historyText, "Current locale:", window.i18n.currentLocale);
                separatorSpan.textContent = historyText;
                separator.appendChild(separatorSpan);
                messagesContainer.appendChild(separator);
                messageHistorySeparator = separator;
                
                // Display messages in chronological order (oldest first)
                messages.reverse().forEach(message => {
                    displayMessage(message);
                });
                
                // Add a separator for new messages
                const newMessagesSeparatorElement = document.createElement("div");
                newMessagesSeparatorElement.className = "new-messages-separator";
                const newMessagesSpan = document.createElement("span");
                const newMessagesText = t("ui.new.messages");
                console.log("New messages separator text:", newMessagesText, "Current locale:", window.i18n.currentLocale);
                newMessagesSpan.textContent = newMessagesText;
                newMessagesSeparatorElement.appendChild(newMessagesSpan);
                messagesContainer.appendChild(newMessagesSeparatorElement);
                newMessagesSeparator = newMessagesSeparatorElement;
            }
            
            // Auto-scroll to bottom
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        })
        .catch(error => {
            console.error("Error fetching message history:", error);
            showError("ui.error.history.failed");
        });
}

/**
 * Connect or disconnect from the WebSocket server
 */
function connect() {
    // If already connected, disconnect
    if (ws && ws.readyState === WebSocket.OPEN) {
        disconnect();
        return;
    }
    
    // Validate username
    const name = nameInput.value.trim();
    if (!name) {
        showError("ui.error.name.required");
        return;
    }
    
    // Update UI to connecting state
    updateConnectionStatus("connecting");
    
    // Get WebSocket URL from the page
    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${wsProtocol}//${window.location.host}/ws/chat`;
    
    // Create WebSocket connection
    try {
        ws = new WebSocket(wsUrl);
    } catch (error) {
        console.error("WebSocket connection error:", error);
        updateConnectionStatus("disconnected");
        showError("ui.error.connection.failed", error.message);
        return;
    }
    
    // WebSocket event handlers
    ws.onopen = function() {
        console.log("Connected to WebSocket server");
        updateConnectionStatus("connected");
        
        // Enable message input but keep send button disabled until text is entered
        messageInput.disabled = false;
        sendButton.disabled = true;
        
        // Change connect button to disconnect
        connectButton.textContent = t("ui.button.disconnect");
        connectButton.setAttribute("data-i18n", "ui.button.disconnect");
        connectButton.disabled = false;
        
        // Disable name input
        nameInput.disabled = true;
        
        // Show online users counter
        document.getElementById("onlineUsers").style.display = "block";
        
        // Send initial message with user name to register in the session
        const initialMessage = {
            name: name,
            message: "",
            type: "JOIN"
        };
        
        try {
            ws.send(JSON.stringify(initialMessage));
        } catch (error) {
            console.error("Error sending initial message:", error);
            showError("ui.error.send.failed", error.message);
        }
        
        // Load message history
        fetchMessageHistory();
    };
    
    ws.onmessage = function(event) {
        try {
            const data = JSON.parse(event.data);
            console.log("Received message:", data);
            
            // Handle user count updates
            if (data.type === "USER_COUNT") {
                // Use the count field directly
                updateOnlineUsers(data.count);
            } else if (data.type === "ERROR") {
                // Display error message in the UI
                showError(data.message);
                // Also add it to the chat as a system message
                displayMessage(data);
            } else if (data.type === "CHAT" && (!data.message || data.message.trim() === "")) {
                // Skip empty chat messages
                console.log("Skipping empty message");
                return;
            } else {
                // Display chat message
                displayMessage(data);
            }
        } catch (error) {
            console.error("Error processing message:", error);
            showError("chat.message.error.processing");
        }
    };
    
    ws.onclose = function() {
        console.log("Disconnected from WebSocket server");
        updateConnectionStatus("disconnected");
        
        // Reset UI to disconnected state
        resetUIAfterDisconnect();
    };
    
    ws.onerror = function(error) {
        console.error("WebSocket error:", error);
        showError("ui.error.websocket");
    };
}

/**
 * Disconnect from the WebSocket server
 */
function disconnect() {
    if (ws && ws.readyState === WebSocket.OPEN) {
        // Send leave message before closing
        try {
            const leaveMessage = {
                name: nameInput.value.trim(),
                message: "",
                type: "LEAVE"
            };
            ws.send(JSON.stringify(leaveMessage));
        } catch (error) {
            console.error("Error sending leave message:", error);
        }
        
        // Close the connection
        ws.close();
    }
    
    // Reset UI to disconnected state
    resetUIAfterDisconnect();
}

/**
 * Reset UI elements after disconnection
 */
function resetUIAfterDisconnect() {
    // Disable message input and send button
    messageInput.disabled = true;
    sendButton.disabled = true;
    
    // Enable name input and reset connect button
    nameInput.disabled = false;
    connectButton.textContent = t("ui.button.connect");
    connectButton.setAttribute("data-i18n", "ui.button.connect");
    connectButton.disabled = false;
    
    // Update connection status
    updateConnectionStatus("disconnected");
}

/**
 * Format ISO timestamp to readable time using Intl API
 */
function formatTimestamp(isoString) {
    try {
        const date = new Date(isoString);
        
        // Use i18n date formatter if available
        if (window.i18n && window.i18n.formatDate) {
            return window.i18n.formatDate(date);
        }
        
        // Fallback to browser's toLocaleTimeString
        return date.toLocaleTimeString();
    } catch (error) {
        console.error("Error formatting timestamp:", error);
        return isoString; // Return original string if parsing fails
    }
}

/**
 * Display a message in the chat
 */
function printMessage(data) {
    try {
        displayMessage(data);
    } catch (error) {
        console.error("Error displaying message:", error);
        showError("ui.error.display.message");
    }
}

/**
 * Display a message object in the chat
 */
function displayMessage(messageData) {
    const messagesContainer = document.getElementById("messages");
    if (!messagesContainer) return;
    
    const messageElement = document.createElement("div");
    messageElement.className = "message";
    
    // Add appropriate class based on message type
    switch (messageData.type) {
        case "JOIN":
            messageElement.classList.add("join");
            break;
        case "LEAVE":
            messageElement.classList.add("leave");
            break;
        case "CHAT":
            messageElement.classList.add("chat");
            break;
        default:
            messageElement.classList.add("system");
    }
    
    // Create message header (sender + time)
    const headerElement = document.createElement("div");
    headerElement.className = "message-header";
    
    // Add sender name
    const senderElement = document.createElement("span");
    senderElement.className = "sender";
    senderElement.textContent = messageData.name || t("chat.message.system");
    headerElement.appendChild(senderElement);
    
    // Add timestamp if available
    if (messageData.timestamp) {
        const timeElement = document.createElement("span");
        timeElement.className = "time";
        timeElement.textContent = formatTimestamp(messageData.timestamp);
        headerElement.appendChild(timeElement);
    }
    
    messageElement.appendChild(headerElement);
    
    // Create message content
    const contentElement = document.createElement("div");
    contentElement.className = "content";
    
    // Format content based on message type
    switch (messageData.type) {
        case "JOIN":
            contentElement.textContent = t("chat.message.join", messageData.name);
            break;
        case "LEAVE":
            contentElement.textContent = t("chat.message.leave", messageData.name);
            break;
        default:
            contentElement.textContent = messageData.message;
            break;
    }
    
    messageElement.appendChild(contentElement);
    messagesContainer.appendChild(messageElement);
    
    // Auto-scroll to bottom
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

/**
 * Send a message to the group chat
 */
function sendToGroupChat() {
    // Check connection status
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        showError("ui.error.not.connected");
        return;
    }
    
    // Get message content
    const content = messageInput.value.trim();
    if (!content) {
        showError("ui.error.message.required");
        return;
    }
    
    // Create message object
    const chatMessage = {
        name: nameInput.value.trim(),
        message: content,
        type: "CHAT"
    };
    
    // Send message
    try {
        ws.send(JSON.stringify(chatMessage));
        messageInput.value = ""; // Clear input field
        messageInput.focus();
        
        // Disable send button after sending (until new content is entered)
        sendButton.disabled = true;
    } catch (error) {
        console.error("Error sending message:", error);
        showError("ui.error.send.failed", error.message);
    }
}

// Listen for language changes and update separators
document.addEventListener('i18n:updated', function() {
    // Update message history separator if it exists
    if (messageHistorySeparator) {
        const span = messageHistorySeparator.querySelector('span');
        if (span) {
            span.textContent = t("ui.message.history");
        }
    }
    
    // Update new messages separator if it exists
    if (newMessagesSeparator) {
        const span = newMessagesSeparator.querySelector('span');
        if (span) {
            span.textContent = t("ui.new.messages");
        }
    }
});

// Close WebSocket connection when window is closed
window.addEventListener("beforeunload", function() {
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.close();
    }
});
