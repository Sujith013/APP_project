function render_search()
{
  document.getElementById('searchForm').addEventListener('submit', function(event) {
      event.preventDefault();

      const form = this;
      const formData = new FormData(form);
      const searchParams = new URLSearchParams();

      // Convert FormData to URLSearchParams
      for (const pair of formData) {
          searchParams.append(pair[0], pair[1]);
      }

      webSocketURL = "ws://localhost:9000/searchWS?"+searchParams.toString();

        const socket = new WebSocket(webSocketURL);

         socket.onopen = () => {
                    console.log("WebSocket connection opened.");
                };

          socket.onmessage = (event) => {
              console.log(searchParams.toString());
              const responseData = JSON.parse(event.data);
              const sentiment = responseData.senti; // Sentiment value
              const data = responseData.data;
               console.log(event.data)// Data for the videos
              printOutput(data, sentiment, searchParams.toString());
          }

        socket.onerror = function(error) {
            console.error("WebSocket error:", error);
        };

        socket.onclose = function() {
            console.log("WebSocket connection closed.");
        };
    });
}


function printOutput(data,sentiment,searchParams)
{
    const sR = document.getElementById('searchResults');

    var newHead = document.createElement("p");
    var headVal = "<b><u>Search Terms</u>:</b>"+searchParams.split("=")[1].replaceAll("+"," ")+"  "+sentiment;
    newHead.innerHTML = headVal;
    newHead.classList.add("list_1");

    var f = -1;

    for(var i=0;i<sR.children.length;i++)
    {
       if(sR.children[i].tagName=="P")
       {
           console.log(sR.children[i].innerHTML.split("</b>")[1]);
           console.log(sR.children[i].innerHTML.split("</b>")[1].slice(0,-5));

           if(sR.children[i].innerHTML.split("</b>")[1].slice(0,-5)==searchParams.split("=")[1].replaceAll("+"," "))
            {
               f = i;
               break;
            }
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

    var count = 0;
    var index = 0;

    for(var i=0;i<sR.children.length;i++)
    {
      if(sR.children[i].tagName=="P")
        count += 1;

      if(count==11)
      {
         index=i;
         break;
      }
    }

    if(count==11)
    {
    while(sR.children.length!=index)
        sR.removeChild(sR.children[sR.children.length-1])
    }
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
