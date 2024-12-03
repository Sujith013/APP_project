function wordStats() {
    const webSocketURL = "ws://localhost:9000/wordStatsWS"; // WebSocket URL for word statistics
    const socket = new WebSocket(webSocketURL);

    // When WebSocket connection is opened
    socket.onopen = () => {
        console.log("WebSocket connection opened for word statistics.");
        const request = { message: "Request word stats" };
        socket.send(JSON.stringify(request));
    };

    // When a message is received from the WebSocket server
    socket.onmessage = (event) => {
        console.log("Raw WebSocket data received:", event.data); // Log raw data for debugging

        try {
            // Parse the incoming JSON response
            const data = JSON.parse(event.data);
            console.log("Parsed JSON data for word stats:", data); // Log parsed data for debugging

            // Handle errors from the server (e.g., missing descriptions)
            if (data.error) {
                console.error("Error from server:", data.error);
                alert("Error from server: " + data.error);
                return;
            }

            // Handle valid word stats if available
            if (data && data.wordStats && Object.keys(data.wordStats).length > 0) {
                const wordStatsDiv = document.getElementById("wordStatsResponse");
                if (wordStatsDiv) {
                    wordStatsDiv.innerHTML = "<b>Word Statistics:</b>";
                    const wordStatsTable = document.createElement("table");
                    wordStatsTable.classList.add("wordStatsTable");

                    Object.entries(data.wordStats).forEach(([word, count]) => {
                        const row = document.createElement("tr");

                        const wordCell = document.createElement("td");
                        wordCell.innerText = word;
                        wordCell.classList.add("wordCell");

                        const countCell = document.createElement("td");
                        countCell.innerText = count;
                        countCell.classList.add("countCell");

                        row.appendChild(wordCell);
                        row.appendChild(countCell);
                        wordStatsTable.appendChild(row);
                    });

                    wordStatsDiv.appendChild(wordStatsTable);
                }
            } else {
                console.log("Received empty or invalid word statistics data.");
                alert("No word statistics available at the moment.");
            }

            // Handle video descriptions if available
            if (data.videoDescriptions && data.videoDescriptions.length > 0) {
                const videosContainer = document.getElementById("videoDescriptions");
                videosContainer.innerHTML = ""; // Clear previous video descriptions

                data.videoDescriptions.forEach((video) => {
                    const videoDiv = document.createElement("div");
                    videoDiv.classList.add("video-item");
                    videoDiv.style.cssText = "border: 1px solid #ddd; margin: 10px; padding: 10px; width: 300px;";

                    const videoContent = `
                        <p><b>Title:</b> <a href="${video[1]}" target="_blank">${video[0]}</a></p>
                        <p><b>Published At:</b> ${video[2]}</p> <!-- Published date -->
                        <p><b>Channel:</b> <a href="${video[6]}" target="_blank">${video[3]}</a></p>
                        <p><b>Description:</b> ${video[4] || 'No description available'}</p> <!-- Video description -->
                        <img src="${video[5]}" alt="Video Thumbnail" width="120" height="80">
                    `;

                    videoDiv.innerHTML = videoContent;
                    videosContainer.appendChild(videoDiv);
                });
            } else {
                console.log("No video descriptions available.");
                alert("No video descriptions available.");
            }

        } catch (error) {
            console.error("Error parsing JSON:", error, "Raw data:", event.data);
        }
    };

    // Handle WebSocket errors
    socket.onerror = (error) => {
        console.error("WebSocket error:", error);
    };

    // Handle WebSocket connection close
    socket.onclose = () => {
        console.log("WebSocket connection closed.");
    };
}
