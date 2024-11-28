function render_search()
{
   var searchHistory = [];

  document.getElementById('searchForm').addEventListener('submit', function(event) {
      event.preventDefault();

      const form = this;
      const formData = new FormData(form);
      const searchParams = new URLSearchParams();

      // Convert FormData to URLSearchParams
      for (const pair of formData) {
          searchParams.append(pair[0], pair[1]);
      }

      searchHistory[searchHistory.length] = searchParams.toString();

      webSocketURL = "ws://localhost:9000/searchWS?"+searchParams.toString();

        const socket = new WebSocket(webSocketURL);

        var searchHistory = "";

         socket.onopen = () => {
                    console.log("WebSocket connection opened.");
                };

          socket.onmessage = (event) => {
              console.log(searchParams.toString);
              console.log(event);
              printOutput(event.data,searchParams.toString(),searchHistory);
          }

        socket.onerror = function(error) {
            console.error("WebSocket error:", error);
        };

        socket.onclose = function() {
            console.log("WebSocket connection closed.");
        };
    });
}


function printOutput(data,searchParams)
{
    const sR = document.getElementById('searchResults');

    var newHead = document.createElement("p");
    var headVal = "<b><u>Search Terms</u>:</b> "+searchParams.toString().split("=")[1].replaceAll("+"," ")+"  "+sentiment+"<a target=\"_blank\" href=\"/wordStats/"+stats_number+"\">Word_Stats</a>";
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

    console.log(count+" "+index);
    console.log(sR.children.length);

    if(count==11)
    {
    while(sR.children.length!=index)
        sR.removeChild(sR.children[sR.children.length-1])
    }

            console.log(sR.children.length);
}
