const username = prompt("Please enter your username:");

var socket;
var stompClient;
var ip;

//https://www.w3schools.com/graphics/canvas_drawing.asp
const canvas = document.getElementById("myCanvas");
const ctx = canvas.getContext("2d");

// Source - https://stackoverflow.com/a/11381730
var zoomAdditive = 0;
window.zoomScalar = function() {
    if (window.innerWidth < 1000 && window.innerHeight < 1000){
        return Math.max(Math.min(.5 + zoomAdditive,10),.25);
    }
    return Math.max(Math.min(1 + zoomAdditive,10),.25);
};

canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

var screenDict = {};
var toSend = {};

toSend["username"] = username;
toSend["windowCenterX"] = 0;
toSend["windowCenterY"] = 0;
toSend["windowWidth"] = window.innerWidth * 1/window.zoomScalar();
toSend["windowHeight"] = window.innerHeight * 1/window.zoomScalar();

//https://stackoverflow.com/a/322827
var mouseDown = 0;
var mouseXInit = 0;
var mouseYInit = 0;

addEventListener("pointerdown", (event) => { 
    ++mouseDown;
    mouseXInit = event.clientX;
    mouseYInit = event.clientY;
})

addEventListener("pointerup", (event) => { 
    --mouseDown;
})

addEventListener("pointercancel", (event) => { 
    --mouseDown;
})

addEventListener("pointermove", (event) => { 
    if (mouseDown == 1){
        toSend["windowCenterX"] = toSend["windowCenterX"] + (mouseXInit - event.clientX)*1/window.zoomScalar();
        toSend["windowCenterY"] = toSend["windowCenterY"] + (mouseYInit - event.clientY)*1/window.zoomScalar();
        mouseXInit = event.clientX;
        mouseYInit = event.clientY;
    }
})

//https://melin.vercel.app/blog/2026-08-28-practical-notes-on-javascript-events-for-two-finger-image-zoom-o#detecting-trackpad-pinch-zoom-on-mac
addEventListener('wheel', function (event) {
    var divisor = 10;
    if (!event.ctrlKey){
        divisor = 300;
    }
    if (!event.deltaY) {
        return;
    }

    event.preventDefault();

    zoomAdditive -=(event.deltaY/divisor);
    zoomAdditive = Math.max(Math.min(zoomAdditive, 10), -.25);
}, {
    passive: false
});

var arrowUp = false;
var arrowLeft = false;
var arrowRight = false;
var arrowDown = false;
document.addEventListener('keydown', (event) => {
    if (event.key === 'ArrowUp') {
        arrowUp = true;
    }
    if (event.key === 'ArrowLeft') {
        arrowLeft = true;
    }
    if (event.key === 'ArrowRight') {
        arrowRight = true;
    }
    if (event.key === 'ArrowDown') {
        arrowDown = true;
    }
});

document.addEventListener('keyup', (event) => {
    if (event.key === 'ArrowUp') {
        arrowUp = false;
    }
    if (event.key === 'ArrowLeft') {
        arrowLeft = false;
    }
    if (event.key === 'ArrowRight') {
        arrowRight = false;
    }
    if (event.key === 'ArrowDown') {
        arrowDown = false;
    }
});

start();

async function getIP() {
    try {
        const response = await fetch('https://api.ipify.org?format=json');
        const data = await response.json();
        return data.ip;
    } catch (error) {
        return 'err';
    }
}

async function start() {
    ip = await getIP();
    var totalPath = "/"+username;
    toSend["totalPath"] = totalPath;
    socket = new SockJS('/data');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/server/'+totalPath, function (message) {
            recieveMessage(message);
        })
        stompClient.send("/app/ip", {}, JSON.stringify(totalPath));
    })
}

function sendData() {
    stompClient.send("/app/data", {}, JSON.stringify(toSend));
}

//Plus https://gist.github.com/asidko/9c7064027039411a11323eaf7d8ea2a4
// const decompress = base64string => {
//     const bytes = Uint8Array.from(atob(base64string), c => c.charCodeAt(0));
//     const cs = new DecompressionStream('gzip');
//     const writer = cs.writable.getWriter();
//     writer.write(bytes);
//     writer.close();
//     return new Response(cs.readable).arrayBuffer().then(function (arrayBuffer) {
//         return new TextDecoder().decode(arrayBuffer);
//     });
// }

//https://cdn.jsdelivr.net/npm/snappyjs@0.7.0/
const decompress = base64string => {
    const bytes = Uint8Array.from(atob(base64string), c => c.charCodeAt(0));
    var uncompressed;
    try {
        uncompressed = uncompress(bytes, 100000);
    } catch (error) {
        uncompressed = new TextEncoder().encode("{}");
    }
    return new TextDecoder().decode(uncompressed);
}

// Source - https://stackoverflow.com/a/68829631
// Posted by joe, modified by community. See post 'Timeline' for change history
// Retrieved 2026-09-07, License - CC BY-SA 4.0
async function recieveMessage(msg) {
    if (typeof msg == "undefined" || Object.keys(msg).length == 0){
        return;
    }
    var decompressedValue = await decompress(msg.body);
    var msgDecompressed = JSON.parse(decompressedValue);
    screenDict = msgDecompressed;
    // screenDict = ON.parse(msg.body);
    drawCreatures();
    recheck();
    arrowkeys();
    sendData();
}

function recheck(){
    if (canvas.width != window.innerWidth || canvas.height != window.innerHeight){
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    }
    toSend["windowWidth"] = window.innerWidth * 1/window.zoomScalar();
    toSend["windowHeight"] = window.innerHeight * 1/window.zoomScalar();
}

function arrowkeys(){
    var speed = 5;
    var vert = 0;
    var horiz = 0;
    if (arrowUp){
        vert-=1;
    }
    if (arrowDown){
        vert+=1;
    }
    if (arrowLeft){
        horiz-=1;
    }
    if (arrowRight){
        horiz+=1;
    }
    toSend["windowCenterX"]+=(horiz*5);
    toSend["windowCenterY"]+=(vert*5);
}

function drawCreatures(){
    ctx.resetTransform();
    ctx.clearRect(0,0, canvas.width, canvas.height);
    
    ctx.save();
    ctx.translate(canvas.width / 2, canvas.height / 2);
    ctx.scale(window.zoomScalar(),window.zoomScalar());
    
    
    if (screenDict["Creatures"] == null){
        return;
    }

    for (var i = 0; i < screenDict["Creatures"].length; i++){
        var creature = screenDict["Creatures"][i];
        var species = screenDict["Species"][1];
        const innerRGB = `rgb(${creature[2]} ${creature[3]} ${creature[4]})`; 
        const outerRGB = `rgb(${creature[5]} ${creature[6]} ${creature[7]})`; 
        if (creature[11] == 0){
            drawCircle(creature[0] - toSend["windowCenterX"], creature[1] - toSend["windowCenterY"], creature[10], innerRGB, outerRGB);
        }else if (creature[11] == 1){
            drawTriangle(creature[0] - toSend["windowCenterX"], creature[1] - toSend["windowCenterY"], creature[10], creature[2], innerRGB, outerRGB)
        }
    }

    ctx.restore();
}

function drawCircle(x, y, radius, innerColor, outerColor){
    ctx.beginPath();
    ctx.arc(x, y, radius, 0, 2 * Math.PI);
    ctx.fillStyle = innerColor;
    ctx.fill();
    ctx.lineWidth = 4;
    ctx.strokeStyle = outerColor;
    ctx.stroke();
}

function drawTriangle(x, y, radius, orientation, innerColor, outerColor){
    ctx.beginPath();
    var cos1 = Math.cos(2*Math.PI*orientation/360.0);
    var sin1 = Math.sin(2*Math.PI*orientation/360.0);
    var cos2 = Math.cos(2*Math.PI*(orientation+120)/360.0);
    var sin2 = Math.sin(2*Math.PI*(orientation+120)/360.0);
    var cos3 = Math.cos(2*Math.PI*(orientation+240)/360.0);
    var sin3 = Math.sin(2*Math.PI*(orientation+240)/360.0);
    ctx.moveTo(x + radius * cos1, y + radius * sin1);
    ctx.lineTo(x + radius * cos2, y + radius * sin2);
    ctx.lineTo(x + radius * cos3, y + radius * sin3);
    ctx.lineTo(x + radius * cos1, y + radius * sin1);
    ctx.fillStyle = innerColor;
    ctx.fill();
    ctx.moveTo(x + radius * cos1, y + radius * sin1);
    ctx.lineTo(x + radius * cos2, y + radius * sin2);
    ctx.lineTo(x + radius * cos3, y + radius * sin3);
    ctx.lineTo(x + radius * cos1, y + radius * sin1);
    ctx.lineWidth = 4;
    ctx.strokeStyle = outerColor;
    ctx.stroke();
}