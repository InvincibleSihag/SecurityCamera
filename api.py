import flask,os
import cv2,imutils
from flask import request, jsonify,send_file,render_template, Response
import threading
import datetime, time
import numpy as np
outputFrame = None
faceDetectionFrame = None
net = cv2.dnn.readNetFromCaffe("deploy.prototxt","res10_300x300_ssd_iter_140000.caffemodel")
lock = threading.Lock()



def faceDetection():
    print("Face Detection Started")
    global faceDetectionFrame,lock
    detectionFrame = None
    while True:
        #print(faceDetectionFrame)
        while True:
            with lock:
                if(faceDetectionFrame is None):
                    continue
                else:
                    detectionFrame = faceDetectionFrame.copy()
                    break
        print("Started")
        with lock:
            detectionFrame = faceDetectionFrame.copy()
        detectionFrame = imutils.resize(detectionFrame, width=400)
        (h, w) = faceDetectionFrame.shape[:2]
        blob = cv2.dnn.blobFromImage(cv2.resize(detectionFrame, (300, 300)), 1.0,(300, 300), (104.0, 177.0, 123.0))
        net.setInput(blob)
        detections = net.forward()
        for i in range(0, detections.shape[2]):
            confidence = detections[0, 0, i, 2]
            #print(confidence)
            if confidence < 0.5:
                continue
            box = detections[0, 0, i, 3:7] * np.array([w, h, w, h])
            (startX, startY, endX, endY) = box.astype("int")
            text = "{:.2f}%".format(confidence * 100)
            y = startY - 10 if startY - 10 > 10 else startY + 10
            cv2.rectangle(detectionFrame, (startX, startY), (endX, endY),
                            (0, 0, 255), 2)
            cv2.putText(detectionFrame, text, (startX, y),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.45, (0, 0, 255), 2)
            cv2.putText(detectionFrame, datetime.datetime.now().strftime("%A %d %B %Y %I:%M:%S%p"),
                        (10, detectionFrame.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)
            cv2.imshow("Face",detectionFrame)
            #cv2.imwrite('H:/SuperProjects/MinorProject/Opencv-Vision/Faces/face'+str(datetime.datetime.now()).replace(" ","")+'.jpg',faceDetectionFrame)


app = flask.Flask(__name__)
app.config["DEBUG"] = True


@app.route('/', methods=['GET'])
def home():
    return '''<h1>Distant Reading Archive</h1>
<p>This is API for Security Camera created by Invincible Team.</p>'''

@app.route('/api/videoStream/',methods=['GET'])

def index():
    # rendering webpage
    return render_template("index.html")

                        
def detectMotion():
    global outputFrame,lock,faceDetectionFrame
    while True:
        cap = cv2.VideoCapture(0)
        firstFrame = None
        startTime = datetime.datetime.now()
        startTime2 = time.time()
        frame_width = int(cap.get(3))
        frame_height = int(cap.get(4))
        try:
            os.makedirs(
                "H:/SuperProjects/MinorProject/Opencv-Vision/MotionVideos/" + str(datetime.datetime.now().date()))
        except OSError as error:
            print(error)
        try:
            os.makedirs(
                "H:/SuperProjects/MinorProject/Opencv-Vision/Faces/" + str(datetime.datetime.now().date()))
        except OSError as error:
            print(error)
            
        print(str(startTime).replace(" ", '-').replace('.', '-'))
        out = cv2.VideoWriter("MotionVideos/" + str(datetime.datetime.now().date()) + "/" + (
            str(startTime).replace(" ", '-').replace(".", '-').replace(":", 'I')) + '.avi',
                              cv2.VideoWriter_fourcc('M', 'J', 'P', 'G'), 60, (frame_width, frame_height))
        firstTime = 0
        endTime = 0
        # haar_cascade_face = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
        while True:
            ret, frame = cap.read()
            faceFrame = frame.copy()
            grayForFace = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            #  faces_rects = haar_cascade_face.detectMultiScale(grayForFace, scaleFactor=1.2, minNeighbors=5);
            gray = cv2.GaussianBlur(grayForFace, (35, 35), 0)
            a = time.time()
            # if the first frame is None, initialize it
            if a - firstTime > 5.0:
                firstTime = time.time()
                firstFrame = gray
                continue

            frameDelta = cv2.absdiff(firstFrame, gray)
            thresh = cv2.threshold(frameDelta, 25, 255, cv2.THRESH_BINARY)[1]
            thresh = cv2.dilate(thresh, None, iterations=2)
            cnts, hierarchy = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL,
                                               cv2.CHAIN_APPROX_SIMPLE)
            # for (x, y, w, h) in faces_rects:
            # cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
            endTime = datetime.datetime.now()
            for c in cnts:
                if cv2.contourArea(c) < 3000:
                    continue
                else:
                    (x, y, w, h) = cv2.boundingRect(c)
                    cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
                    text = "Occupied"
                    cv2.putText(frame, endTime.strftime("%A %d %B %Y %I:%M:%S%p"),
                                (10, frame.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)
                    frame = cv2.resize(frame, (640, 480))
                    out.write(frame)
            with lock:
                outputFrame = frame.copy()
                #faceDetectionFrame = faceFrame.copy()
            #cv2.imshow('me', frame)
            #print(endTime.hour)

            # FACE DETECTION STARTS FROM HERE
            detectionFrame = imutils.resize(faceFrame, width=400)
            (h, w) = detectionFrame.shape[:2]
            blob = cv2.dnn.blobFromImage(cv2.resize(detectionFrame, (300, 300)), 1.0,(300, 300), (104.0, 177.0, 123.0))
            net.setInput(blob)
            detections = net.forward()
            for i in range(0, detections.shape[2]):
                confidence = detections[0, 0, i, 2]
                #print(confidence)
                if confidence < 0.5:
                    continue
                box = detections[0, 0, i, 3:7] * np.array([w, h, w, h])
                (startX, startY, endX, endY) = box.astype("int")
                text = "{:.2f}%".format(confidence * 100)
                y = startY - 10 if startY - 10 > 10 else startY + 10
                cv2.rectangle(detectionFrame, (startX, startY), (endX, endY),
                                (0, 0, 255), 2)
                cv2.putText(detectionFrame, text, (startX, y),
                                cv2.FONT_HERSHEY_SIMPLEX, 0.45, (0, 0, 255), 2)
                cv2.putText(detectionFrame, datetime.datetime.now().strftime("%A %d %B %Y %I:%M:%S%p"),
                            (10, detectionFrame.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)
                cv2.imwrite("Faces/" +str(datetime.datetime.now().date())+"/"+str(datetime.datetime.now()).replace(" ", '-').replace(".", '-').replace(":", 'I')+".jpeg" ,detectionFrame)
            cv2.imshow("face Detection", detectionFrame)
            cv2.imshow("motion Detection",frame)
            if (endTime.hour == 0):
                out.release()
                try:
                    os.makedirs("H:/SuperProjects/MinorProject/Opencv-Vision/MotionVideos/" + str(
                        datetime.datetime.now().date()))
                    os.makedirs("H:/SuperProjects/MinorProject/Opencv-Vision/Faces/" + str(datetime.datetime.now().date()))
                except OSError as error:
                    print(error)
                out = cv2.VideoWriter("MotionVideos/" + str(datetime.datetime.now().date()) + "/" + (
                    str(startTime).replace(" ", '-').replace(".", '-').replace(":", 'I')) + '.avi',
                                      cv2.VideoWriter_fourcc('M', 'J', 'P', 'G'), 60, (frame_width, frame_height))
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break
        cap.release()
        out.release()
        cv2.destroyAllWindows()

def gen():
    global outputFrame, lock
    while True:
        with lock:
            if outputFrame is None:
                continue
            (flag, encodedImage) = cv2.imencode(".jpg", outputFrame)
            if not flag:
                continue
        yield(b'--frame\r\n' b'Content-Type: image/jpeg\r\n\r\n' + 
			bytearray(encodedImage) + b'\r\n')
    
@app.route('/video_feed')
def video_feed():
    return Response(gen(),
                    mimetype='multipart/x-mixed-replace; boundary=frame')

@app.route('/api/v1/resources/', methods=['GET'])
def get_FolderFiles():
    stringFiles = ""
    day = request.args.get('day')
    files = os.listdir("H:/SuperProjects/MinorProject/Opencv-Vision/MotionVideos/"+str(day))
    for file in files:
        stringFiles += file + " "
    return str(stringFiles)

@app.route('/api/v1/resources/folders')
def getFolders():
    stringFolders = ""
    folders = os.listdir("H:/SuperProjects/MinorProject/Opencv-Vision/MotionVideos/")
    for folder in folders:
        stringFolders += folder + " "
    return str(stringFolders)

@app.route('/api/v1/resources/foldersFace')
def getFaceFolders():
    stringFolders = ""
    folders = os.listdir("H:/SuperProjects/MinorProject/Opencv-Vision/Faces/")
    for folder in folders:
        stringFolders += folder + " "
    return str(stringFolders)

@app.route('/api/v1/resources/folders/facesFiles', methods=['GET'])
def get_FolderFacesFiles():
    stringFiles = ""
    day = request.args.get('faces')
    files = os.listdir("H:/SuperProjects/MinorProject/Opencv-Vision/Faces/"+str(day))
    for file in files:
        stringFiles += file + " "
    return str(stringFiles)

@app.route('/api/v1/resources/files/faces/',methods = ['GET'])
def getFaces():
    face = request.args.get('faces')
    directory = "H:/SuperProjects/MinorProject/Opencv-Vision/Faces/" + str(face)
    return send_file(directory)

@app.route('/api/v1/resources/files/videoFile/',methods = ['GET'])
def getVideo():
    video = request.args.get('video')
    directory = "H:/SuperProjects/MinorProject/Opencv-Vision/MotionVideos/" + video
    return send_file(directory)

t = threading.Thread(target=detectMotion,args=())
#threading.Thread(target=faceDetection,args=()).start()
t.daemon = True
t.start()
app.run(debug= True, host = '0.0.0.0',threaded=True,use_reloader=False)
