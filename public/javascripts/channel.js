function render_channel_profile(channelId) {
    const webSocketURL = `ws://localhost:9000/channelWS?channelId=${channelId}`;
    const socket = new WebSocket(webSocketURL);

    socket.onopen = () => {
        console.log("WebSocket connection opened for Channel Profile.");
    };

    socket.onmessage = (event) => {
        try {
            // Parse JSON response
            const data = JSON.parse(event.data);
            console.log("Received Channel Data:", data);

            // Update Channel Profile Details
            document.getElementById("channelTitle").innerText = data.channelTitle;
            document.getElementById("description").innerText = data.description;
            document.getElementById("subscriberCount").innerText = data.subscriberCount;
            document.getElementById("publishedAt").innerText = `Published At: ${data.publishedAt}`;
            const thumbnail = document.getElementById("thumbnailUrl");
            thumbnail.src = data.thumbnailUrl;
            thumbnail.style.display = "block"; // Show the thumbnail if hidden

            // Update Recent Videos
            const videosContainer = document.getElementById("recentVideos");
            videosContainer.innerHTML = ""; // Clear previous videos

            data.recentVideos.forEach((video) => {
                const videoDiv = document.createElement("div");
                videoDiv.classList.add("video-item");
                videoDiv.style.cssText = "border: 1px solid #ddd; margin: 10px; padding: 10px; width: 250px;";

                const videoContent = `
                    <p><b>Title:</b> <a href="${video[1]}" target="_blank">${video[0]}</a></p>
                    <p><b>Published At:</b> ${video[2]}</p> <!-- Add published date here -->
                    <p><b>Channel:</b> <a href="${video[6]}" target="_blank">${video[3]}</a></p>
                    <p><b>Description:</b> ${video[4]}</p>
                    <img src="${video[5]}" alt="Video Thumbnail" width="120" height="80">
                `;

                videoDiv.innerHTML = videoContent;
                videosContainer.appendChild(videoDiv);
            });
        } catch (error) {
            console.error("Error parsing JSON:", error, "Raw data:", event.data);
        }
    };

    socket.onerror = (error) => {
        console.error("WebSocket error:", error);
    };

    socket.onclose = () => {
        console.log("WebSocket connection closed.");
    };
}
