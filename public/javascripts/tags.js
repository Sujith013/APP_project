function render_tags(videoId)
{
webSocketURL = "ws://localhost:9000/tagsWS?videoId="+videoId
const socket = new WebSocket(webSocketURL);

 socket.onopen = () => {
            console.log("WebSocket connection opened.");
            // Optionally send a message to the server after opening the connection
            socket.send(JSON.stringify({ message: "Hello, server!" }));
        };

 socket.onmessage = (event) => {
           try {
                   // Parse the JSON string into a JavaScript object
                   const data = JSON.parse(event.data);
                   console.log("Received JSON data:", data);

                   document.getElementById("title").innerText = data.videoTitle;
                   document.getElementById("title").href = "https://www.youtube.com/watch?v="+data.videoId;
                   document.getElementById("channel").innerText = data.channelTitle;
                   document.getElementById("channel").href = "https://www.youtube.com/channel/"+data.channelId;
                   document.getElementById("description").innerHTML = "<b>Description:</b>"+data.description+"<br><br>";
                   document.getElementById("thumbnail").src = data.thumbnail;

                    var tags_div = document.getElementById("tagsResponse");
                       var tags = data.tags.split("+");
                       tags_div.innerHTML = "<b>Tags: </b>";
                       for(var i=0;i<tags.length;i++)
                       {
                           newA = document.createElement("a");
                           newA.innerText = tags[i];
                           newA.classList.add("tagsA");
                           newA.setAttribute("href", "/searchTag");
                           newA.addEventListener("click", (event) => {
                               event.preventDefault();
                               const tagId = tags[i];
                               const url = `/searchTag?searchTerm=${encodeURIComponent(tagId)}`;
                               console.log(url);
                               fetch(url)
                                  .then(response => response.json())
                                   .then(data => printOutput(data,tagId))
                                   .catch(error => {
                                       console.error('Error:', error);
                                    }
                               );
                           });
                           tags_div.append(newA);
                       }

               } catch (error) {
                   console.error("Error parsing JSON:", error, "Raw data:", event.data);
               }
         };

         socket.onerror = function(error) {
             console.error("WebSocket error:", error);
         };

         socket.onclose = function() {
             console.log("WebSocket connection closed.");
         };
}

/*
function printOutput(data,tagId)
{
  const sR = document.getElementById('tagsResults');
  var newHead = document.createElement("p");
  var headVal = "<b><u>Tag</u>:</b> "+tagId+"<br>";
  newHead.innerHTML = headVal;
  newHead.classList.add("list_1");
  for(let i=0;i<data.length;i++)
  {
     var newDiv = document.createElement('div');
     var innerVal = "<p class=\"para\">"+(data.length-i) + ". <b>Title:</b><a target=\"_blank\" href=\""+data[i][1]+"\">"+data[i][0]+"</a>, <b>Channel:</b><a target=\"_blank\" href=\""+data[i][5]+"\">"+data[i][2].split("@")[1]+"</a>"+", <b>Description:</b>\""+data[i][3]+"\", <a target=\"_blank\" href=\"/tags/"+data[i][1].split("=")[1]+"\">Tags</a></p></br></br><img alt=\"thumbnail\" src=\""+data[i][4]+"\" width=\"75px\" height=\"50px\" class=\"thumbnail\">";
     newDiv.innerHTML = innerVal;
     newDiv.classList.add("list_1");
     if(sR.firstChild)
         sR.insertBefore(newDiv, sR.firstChild); // Insert before the first child
     else
         sR.appendChild(newDiv);
  }
  if(sR.firstChild)
      sR.insertBefore(newHead, sR.firstChild); // Insert before the first child
  else
      sR.appendChild(newHead);
}*/
