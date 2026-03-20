import React, { useState, useEffect } from "react";

function ChatBot() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [langID,setLangID] = useState("en")
  const [accessToken,setAccessToken] = useState("NA")
  const access_token=Digit.UserService.getUser()?.access_token || "" ;
  // const abc = Digit.UserService.getUser()?.access_token
  // const access_token =""
  const toggleChatbot = () => {
    setIsOpen(!isOpen);
  };

  const handleMessageSend = async () => {
    if (input.trim() !== "") {
      setMessages([...messages, { sender: "user", text: input }]);
      setInput("");
  
      try {
        const response = await fetch('https://oprwrt-demo.ddns.net/chat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ message: input, chatHistory: messages, lang_id: langID, accessToken: access_token }),
        });
        // console.log(body)
  
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
  
        const data = await response.json();
        const botReply = data.response; 
        
        if (data.access_token !== undefined) {
          setAccessToken(data.access_token)
        }
        
        setTimeout(() => {
          setMessages((prevMessages) => [
            ...prevMessages,
            { sender: "bot", text: botReply, lang_id: langID },
          ]);
        }, 1000);
      } catch (error) {
        console.error("Error fetching response:", error);
        // Handle error or fallback message
        setTimeout(() => {
          setMessages((prevMessages) => [
            ...prevMessages,
            { sender: "bot", text: "Sorry, I'm having trouble understanding you right now.", lang_id: langID },
          ]);
        }, 1000);
      }
    }
  };
  

  const handleInputChange = (e) => {
    setInput(e.target.value);
  };

  const handleDropdown = (e) => {
    setLangID(e.target.value)
  };

  const handleChatbotClose = () => {
    setIsOpen(false);
  };

  useEffect(() => {
    if (isOpen) {
      setMessages([
        {
          sender: "bot",
          text: "Welcome to NagarSewa Portal"
          , lang_id: langID
        },
        {
          sender: "bot",
          text: "How may I assist you."
          , lang_id: langID
        },
      ]);
    } else {
      setMessages([]);
    }
  }, [isOpen]);

  const ReplaceURL = (text) => {
    return text.replace(
      /(https?:\/\/[^\s]+)/g,
      '<a href="$1" target="_blank" rel="noopener noreferrer">$1</a>'
    );
  };


  return (
    <div style={{ textAlign: "start" }}>
      {!isOpen && (
        <button
          style={{
            position: "fixed",
            bottom: "20px",
            right: "20px",
            padding: "10px",
            fontSize: "16px",
            cursor: "pointer",
            backgroundColor: "#D40000",
            color: "white",
            border: "none",
            borderRadius: "50%",
            width: "60px",
            height: "60px",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
          }}
          onClick={toggleChatbot}
        >
          <svg id="Layer_1" data-name="Layer 1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 122.88 119.35" fill="#FFFFFF"><title>chatbot</title><path d="M57.49,29.2V23.53a14.41,14.41,0,0,1-2-.93A12.18,12.18,0,0,1,50.44,7.5a12.39,12.39,0,0,1,2.64-3.95A12.21,12.21,0,0,1,57,.92,12,12,0,0,1,61.66,0,12.14,12.14,0,0,1,72.88,7.5a12.14,12.14,0,0,1,0,9.27,12.08,12.08,0,0,1-2.64,3.94l-.06.06a12.74,12.74,0,0,1-2.36,1.83,11.26,11.26,0,0,1-2,.93V29.2H94.3a15.47,15.47,0,0,1,15.42,15.43v2.29H115a7.93,7.93,0,0,1,7.9,7.91V73.2A7.93,7.93,0,0,1,115,81.11h-5.25v2.07A15.48,15.48,0,0,1,94.3,98.61H55.23L31.81,118.72a2.58,2.58,0,0,1-3.65-.29,2.63,2.63,0,0,1-.63-1.85l1.25-18h-.21A15.45,15.45,0,0,1,13.16,83.18V81.11H7.91A7.93,7.93,0,0,1,0,73.2V54.83a7.93,7.93,0,0,1,7.9-7.91h5.26v-2.3A15.45,15.45,0,0,1,28.57,29.2H57.49ZM82.74,47.32a9.36,9.36,0,1,1-9.36,9.36,9.36,9.36,0,0,1,9.36-9.36Zm-42.58,0a9.36,9.36,0,1,1-9.36,9.36,9.36,9.36,0,0,1,9.36-9.36Zm6.38,31.36a2.28,2.28,0,0,1-.38-.38,2.18,2.18,0,0,1-.52-1.36,2.21,2.21,0,0,1,.46-1.39,2.4,2.4,0,0,1,.39-.39,3.22,3.22,0,0,1,3.88-.08A22.36,22.36,0,0,0,56,78.32a14.86,14.86,0,0,0,5.47,1A16.18,16.18,0,0,0,67,78.22,25.39,25.39,0,0,0,72.75,75a3.24,3.24,0,0,1,3.89.18,3,3,0,0,1,.37.41,2.22,2.22,0,0,1,.42,1.4,2.33,2.33,0,0,1-.58,1.35,2.29,2.29,0,0,1-.43.38,30.59,30.59,0,0,1-7.33,4,22.28,22.28,0,0,1-7.53,1.43A21.22,21.22,0,0,1,54,82.87a27.78,27.78,0,0,1-7.41-4.16l0,0ZM94.29,34.4H28.57A10.26,10.26,0,0,0,18.35,44.63V83.18A10.26,10.26,0,0,0,28.57,93.41h3.17a2.61,2.61,0,0,1,2.41,2.77l-1,14.58L52.45,94.15a2.56,2.56,0,0,1,1.83-.75h40a10.26,10.26,0,0,0,10.22-10.23V44.62A10.24,10.24,0,0,0,94.29,34.4Z"/></svg>
        </button>
      )}
      {isOpen && (
        <div
          style={{
            position: "fixed",
            bottom: "20px",
            right: "20px",
            width: "400px",
            maxHeight: "400px",
            height: "500px",
            backgroundColor: "white",
            borderRadius: "8px",
            boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
            display: "flex",
            flexDirection: "column",
            overflow: "hidden",
          }}
        >
          <div
            style={{
              backgroundColor: "#192771",
              color: "white",
              padding: "10px",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <span>NagarSewa Sathi</span>
    
            <div style={{
              backgroundColor: "#192771",
              color: "white",
              padding: "10px",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}>
            <div style={{
              backgroundColor: "white",
              color: "black",
              padding: "10px",
              paddingBottom: "5px",
              paddingTop: "5px",
              marginRight: "10px",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              borderRadius: "10px",
            }}>
            <select value={langID} onChange={handleDropdown}>
              <option value={"en"}>English</option>
              <option value={"hi"} >हिन्दी</option>
            </select>
            </div>
            <button
              onClick={handleChatbotClose}
              style={{
                background: "none",
                border: "none",
                color: "white",
                cursor: "pointer",
              }}
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16.816"
                height="16.816"
                viewBox="0 0 16.816 16.816">
                <path
                  id="Icon_ionic-ios-close-circle"
                  data-name="Icon ionic-ios-close-circle"
                  d="M11.783,3.375a8.408,8.408,0,1,0,8.408,8.408A8.407,8.407,0,0,0,11.783,3.375Zm2.13,11.452-2.13-2.13-2.13,2.13a.646.646,0,1,1-.914-.914l2.13-2.13-2.13-2.13a.646.646,0,0,1,.914-.914l2.13,2.13,2.13-2.13a.646.646,0,0,1,.914.914l-2.13,2.13,2.13,2.13a.649.649,0,0,1,0,.914A.642.642,0,0,1,13.913,14.827Z"
                  transform="translate(-3.375 -3.375)"
                  fill="#fff"
                />
              </svg>
            </button>
              </div>
          </div>
          <div
            style={{
              flex: 1,
              padding: "10px",
              overflowY: "auto",
              backgroundColor: "#f1f1f1",
            }}
          >
            {messages.map((message, index) => (
              <div
                key={index}
                style={{
                  display: "flex",
                  justifyContent:
                    message.sender === "user" ? "flex-end" : "flex-start",
                  marginBottom: "10px",
                }}
              >
                <div
                  style={{
                    maxWidth: "70%",
                    padding: "10px",
                    borderRadius: "10px",
                    backgroundColor:
                      message.sender === "user" ? "#00599f" : "#e4e6eb",
                    color: message.sender === "user" ? "white" : "black",
                    whiteSpace: "pre-wrap",
                    wordWrap: "break-word",
                  }}
                >
                  <div
                    dangerouslySetInnerHTML={{
                      __html: ReplaceURL(message.text),
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
          <div
            style={{
              display: "flex",
              padding: "10px",
              borderTop: "1px solid #ddd",
            }}
          >
            <input
              type="text"
              value={input}
              onChange={handleInputChange}
              onKeyPress={(e) => {
                if (e.key === "Enter") {
                  handleMessageSend();
                }
              }}
              placeholder="Type your message..."
              style={{
                flex: 1,
                padding: "10px",
                borderRadius: "20px",
                border: "1px solid #ddd",
                outline: "none",
              }}
            />
            <button
              onClick={handleMessageSend}
              style={{
                marginLeft: "10px",
                padding: "15px",
                backgroundColor: "#192771",
                color: "white",
                border: "none",
                borderRadius: "50%",
                cursor: "pointer",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                strokeWidth="1.5"
                stroke="currentColor"
                className="size-6"
                width="16"
                height="16"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M6 12 3.269 3.125A59.769 59.769 0 0 1 21.485 12 59.768 59.768 0 0 1 3.27 20.875L5.999 12Zm0 0h7.5"
                />
              </svg>
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default ChatBot;