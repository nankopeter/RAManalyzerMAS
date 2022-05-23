
import threading
import socket
import time


def worker(lock, server_running, message_queue):

    HOST = "localhost"
    PORT = 11250
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    while True:
        try:
            sock.connect((HOST, PORT))
        except:
            print "Connecting..."
            time.sleep(1)
            continue

        print "connected to senzors"
        lock.acquire()
        server_running.get()
        lock.release()

        #server_running.task_done()

        running = True

        while running:
            time.sleep(3)
            try:
                if message_queue.empty():
                    print "nothing in message_queue"
                else:
                    while message_queue.empty() == False:

                        message_to_send = str(message_queue.get())
                        print(message_to_send)
                        module_name = message_to_send.split(' ', 1)[0]
                        sock.sendall(message_to_send + "\n");
                        start = time.time()

                        # sending messages while queue not empty
                        while True:
                            received =sock.recv(1024)
                            if received == (module_name + "\r\n"):
                                print module_name + " sent"
                                break
                            if time.time() > start + 5:
                                print module_name + " NOT sent"
                                break

            except:
                running = False
                sock.close()
                print "Socket closed"
                return
