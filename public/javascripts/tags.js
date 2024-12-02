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

                           newA.href = "/";

                           const tagId = tags[i];

                           newA.addEventListener("click", (event) => {

                           event.preventDefault();
                           console.log(tagId);
                           webSocketURL = "ws://localhost:9000/searchWS?searchTerms="+tagId;
                           console.log(webSocketURL);

                           const socket = new WebSocket(webSocketURL);

                           socket.onopen = () => {
                                  console.log("WebSocket connection opened.");
                           };

                           socket.onmessage = (event) => {
                                    console.log(tagId);
                                    console.log(event.data);
                                    printOutput(JSON.parse(event.data),tagId);
                           }

                           socket.onerror = function(error) {
                                    console.error("WebSocket error:", error);
                           };

                           socket.onclose = function() {
                                   console.log("WebSocket connection closed.");
                                       };
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


function printOutput(data,searchParams)
{
    const sR = document.getElementById('tagsResults');
    console.log(searchParams);
    var newHead = document.createElement("p");
    var headVal = "<b><u>Search Terms</u>:</b>"+searchParams;
    newHead.innerHTML = headVal;
    newHead.classList.add("list_1");

    var f = -1;

    for(var i=0;i<sR.children.length;i++)
    {
       if(sR.children[i].tagName=="P")
           if(sR.children[i].innerHTML.split("</b>")[1]==searchParams)
            {
               f = i;
               break;
            }
    }

    if(f==-1)
    {
    for(let i=0;i<data.length;i++)
    {
       var newDiv = document.createElement('div');

       var innerVal = "<p class=\"para\">"+(data.length-i) + ". <b>Title:</b><a target=\"_blank\" class=\"video_title\" href=\""+data[i][1]+"\">"+data[i][0]+"</a>, <b>Channel:</b><a target=\"_blank\" class=\"channel_title\" href=\""+data[i][5]+"\">"+data[i][2].split("@")[1]+"</a>"+", <b class=\"desc\">Description:\""+data[i][3]+"\",</b> <a target=\"_blank\" class=\"tags_list_replace\" href=\"/tags/"+data[i][1].split("=")[1]+"\">Tags</a></p></br></br><img alt=\"thumbnail\" class=\"image_src\" src=\""+data[i][4]+"\" width=\"75px\" height=\"50px\" class=\"thumbnail\">";

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
    }
    else
    {
    for(var i=(f+1);i<=(f+10);i++)
    {
      sR.children[i].getElementsByClassName("video_title")[0].innerText = data[i-(f+1)][0];
      sR.children[i].getElementsByClassName("video_title")[0].href = data[i-(f+1)][1];
      sR.children[i].getElementsByClassName("channel_title")[0].href = data[i-(f+1)][5];
      sR.children[i].getElementsByClassName("channel_title")[0].innerText = data[i-(f+1)][5].split("@")[1];
      sR.children[i].getElementsByClassName("desc")[0].innerText = data[i-(f+1)][3];
      sR.children[i].getElementsByClassName("image_src")[0].src = data[i-(f+1)][4];
      sR.children[i].getElementsByClassName("tags_list_replace")[0].href = "/tags/"+data[i-(f+1)][1].split("=")[1];
    }
    }
}
