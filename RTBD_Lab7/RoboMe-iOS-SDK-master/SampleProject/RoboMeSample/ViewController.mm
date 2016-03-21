//
//  ViewController.m
//  RoboMeBasicSample
//
//  Copyright (c) 2013 WowWee Group Limited. All rights reserved.
//

#import "ViewController.h"
#import "GCDAsyncSocket.h"
#include <ifaddrs.h>
#include <arpa/inet.h>
#import <SpeechKit/SpeechKit.h>

#define WELCOME_MSG  0
#define ECHO_MSG     1
#define WARNING_MSG  2


#define READ_TIMEOUT 15.0
#define READ_TIMEOUT_EXTENSION 10.0

#define FORMAT(format, ...) [NSString stringWithFormat:(format), ##__VA_ARGS__]
#define PORT 1234

const unsigned char SpeechKitApplicationKey[] = {0x07, 0x91, 0x98, 0x86, 0xab, 0x57, 0xbf, 0x28, 0x71, 0x48, 0xb5, 0x73, 0xab, 0xed, 0x94, 0x48, 0x39, 0xcb, 0xe2, 0x76, 0xdf, 0xce, 0xeb, 0x58, 0x3c, 0x48, 0xca, 0xde, 0x2b, 0x95, 0x28, 0x32, 0x26, 0xc0, 0xb8, 0xd1, 0x18, 0x77, 0xfa, 0x89, 0xbf, 0xaf, 0xfc, 0x6b, 0x9c, 0x91, 0xb3, 0xd5, 0x5c, 0x95, 0x4c, 0x3b, 0x63, 0x37, 0x93, 0x9f, 0x7f, 0x5d, 0x7d, 0xaa, 0xf2, 0xa5, 0xdc, 0x18};


@interface ViewController () {
    dispatch_queue_t socketQueue;
    NSMutableArray *connectedSockets;
    BOOL isRunning;
    
    GCDAsyncSocket *listenSocket;
}

@property (strong, nonatomic) AVSpeechSynthesizer *synthesizer;


@property (nonatomic, strong) RoboMe *roboMe;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [SpeechKit setupWithID:@"NMDPTRIAL_mahidharvarma_gmail_com20150908153035"
                      host:@"sslsandbox.nmdp.nuancemobility.net"
                      port:	443
                    useSSL:YES
                  delegate:nil];

    
    // create RoboMe object
    self.roboMe = [[RoboMe alloc] initWithDelegate: self];
    
    // start listening for events from RoboMe
    [self.roboMe startListening];
    
    // Do any additional setup after loading the view, typically from a nib.
    socketQueue = dispatch_queue_create("socketQueue", NULL);
    
    listenSocket = [[GCDAsyncSocket alloc] initWithDelegate:self delegateQueue:socketQueue];
    
    // Setup an array to store all accepted client connections
    connectedSockets = [[NSMutableArray alloc] initWithCapacity:1];
    
    isRunning = NO;
    
    NSLog(@"%@", [self getIPAddress]);
    
    [self toggleSocketState];   //Statrting the Socket
    self.KafkaInput.returnKeyType=UIReturnKeySearch;
    
}

- (IBAction)recordButtonTapped:(id)sender {
    
    self.recordButton.selected = !self.recordButton.isSelected;
    
    // This will initialize a new speech recognizer instance
    if (self.recordButton.isSelected) {
        self.voiceSearch = [[SKRecognizer alloc] initWithType:SKSearchRecognizerType
                                                    detection:SKShortEndOfSpeechDetection
                                                     language:@"en_US"
                                                     delegate:self];
    }
    
    // This will stop existing speech recognizer processes
    else {
        if (self.voiceSearch) {
            [self.voiceSearch stopRecording];
            [self.voiceSearch cancel];
        }
    }
}


- (void)recognizer:(SKRecognizer *)recognizer didFinishWithResults:(SKRecognition *)results {
    long numOfResults = [results.results count];
    
    if (numOfResults > 0) {
        // update the text of text field with best result from SpeechKit
        self.KafkaInput.text = [results firstResult];
        self.synthesizer = [[AVSpeechSynthesizer alloc] init];
        AVSpeechUtterance *utterance = [[AVSpeechUtterance alloc] initWithString:[results firstResult]];
        utterance.rate = 0.2;
        [self.synthesizer speakUtterance:utterance];
        
    }
    
    self.recordButton.selected = !self.recordButton.isSelected;
    
    if (self.voiceSearch) {
        [self.voiceSearch cancel];
    }
    
}
- (void)recognizer:(SKRecognizer *)recognizer didFinishWithError:(NSError *)error suggestion:(NSString *)suggestion {
    self.recordButton.selected = NO;
    
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error"
                                                    message:[error localizedDescription]
                                                   delegate:nil
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
}

- (IBAction)tappedOnSend:(id)sender {
    if (isRunning) {
        
        NSString *msg = [NSString stringWithFormat:@"%@ \n",self.KafkaInput.text];
        NSData *data = [msg dataUsingEncoding:NSUTF8StringEncoding];
        
        GCDAsyncSocket *socket = [connectedSockets lastObject];
        if (socket != nil) {
            [socket writeData:data withTimeout:-1 tag:ECHO_MSG];
            [self displayText:[NSString stringWithFormat:@"Message sent : %@ \n",msg]];
        } else {
            NSLog(@"Socket not available");
        }
        NSLog(@"Inside Send");
    }
    
}

// Print out given text to text view
- (void)displayText: (NSString *)text {
    NSString *outputTxt = [NSString stringWithFormat: @"%@\n%@", self.outputTextView.text, text];
    
    // print command to output box
    [self.outputTextView setText: outputTxt];
    
    // scroll to bottom
    [self.outputTextView scrollRangeToVisible:NSMakeRange([self.outputTextView.text length], 0)];
}

#pragma mark - RoboMeConnectionDelegate

// Event commands received from RoboMe
- (void)commandReceived:(IncomingRobotCommand)command {
    // Display incoming robot command in text view
    [self displayText: [NSString stringWithFormat: @"Received: %@" ,[RoboMeCommandHelper incomingRobotCommandToString: command]]];
    
    // To check the type of command from RoboMe is a sensor status use the RoboMeCommandHelper class
    if([RoboMeCommandHelper isSensorStatus: command]){
        // Read the sensor status
        SensorStatus *sensors = [RoboMeCommandHelper readSensorStatus: command];
        
        // Update labels
        [self.edgeLabel setText: (sensors.edge ? @"ON" : @"OFF")];
        [self.chest20cmLabel setText: (sensors.chest_20cm ? @"ON" : @"OFF")];
        [self.chest50cmLabel setText: (sensors.chest_50cm ? @"ON" : @"OFF")];
        [self.cheat100cmLabel setText: (sensors.chest_100cm ? @"ON" : @"OFF")];
    }
}

- (void)volumeChanged:(float)volume {
    if([self.roboMe isRoboMeConnected] && volume < 0.75) {
        [self displayText: @"Volume needs to be set above 75% to send commands"];
    }
}

- (void)roboMeConnected {
    [self displayText: @"RoboMe Connected!"];
}

- (void)roboMeDisconnected {
    [self displayText: @"RoboMe Disconnected"];
}

#pragma mark -
#pragma mark User-Defined Robo Movement

- (NSString *)direction:(NSString *)message {
    
    return @"";
}

- (void)perform:(NSString *)command {
    
    NSString *cmd = [command uppercaseString];
    if ([cmd isEqualToString:@"LEFT"]) {
        [self.roboMe sendCommand:kRobot_TurnLeft90Degrees];
    } else if ([cmd isEqualToString:@"RIGHT"]) {
        [self.roboMe sendCommand: kRobot_TurnRight90Degrees];
    } else if ([cmd isEqualToString:@"BACKWARD"]) {
        [self.roboMe sendCommand: kRobot_MoveBackwardFastest];
    } else if ([cmd isEqualToString:@"FORWARD"]) {
        [self.roboMe sendCommand: kRobot_MoveForwardFastest];
    } else if([cmd isEqualToString:@"STOP"]){
        [self.roboMe sendCommand:kRobot_Stop];
    }
}
#pragma mark - Button callbacks

// The methods below send the desired command to RoboMe.
// Typically you would want to start a timer to repeatly send the
// command while the button is held down. For simplicity this wasn't
// included however if you do decide to implement this we recommand
// sending commands every 500ms for smooth movement.
// See RoboMeCommandHelper.h for a full list of robot commands
- (IBAction)moveForwardBtnPressed:(UIButton *)sender {
    // Adds command to the queue to send to the robot
    [self.roboMe sendCommand: kRobot_MoveForwardFastest];
}

- (IBAction)moveBackwardBtnPressed:(UIButton *)sender {
    [self.roboMe sendCommand: kRobot_MoveBackwardFastest];
}

- (IBAction)turnLeftBtnPressed:(UIButton *)sender {
    [self.roboMe sendCommand: kRobot_TurnLeftFastest];
}

- (IBAction)turnRightBtnPressed:(UIButton *)sender {
    [self.roboMe sendCommand: kRobot_TurnRightFastest];
}

- (IBAction)headUpBtnPressed:(UIButton *)sender {
    [self.roboMe sendCommand: kRobot_HeadTiltAllUp];
}

- (IBAction)headDownBtnPressed:(UIButton *)sender {
    [self.roboMe sendCommand: kRobot_HeadTiltAllDown];
}
#pragma mark -
#pragma mark Socket

- (void)toggleSocketState
{
    if(!isRunning)
    {
        NSError *error = nil;
        if(![listenSocket acceptOnPort:PORT error:&error])
        {
            [self log:FORMAT(@"Error starting server: %@", error)];
            return;
        }
        
        [self log:FORMAT(@"Echo server started on port %hu", [listenSocket localPort])];
        isRunning = YES;
    }
    else
    {
        // Stop accepting connections
        [listenSocket disconnect];
        
        // Stop any client connections
        @synchronized(connectedSockets)
        {
            NSUInteger i;
            for (i = 0; i < [connectedSockets count]; i++)
            {
                // Call disconnect on the socket,
                // which will invoke the socketDidDisconnect: method,
                // which will remove the socket from the list.
                [[connectedSockets objectAtIndex:i] disconnect];
            }
        }
        
        [self log:@"Stopped Echo server"];
        isRunning = false;
    }
}

- (void)log:(NSString *)msg {
    NSLog(@"%@", msg);
}

- (NSString *)getIPAddress
{
    NSString *address = @"error";
    struct ifaddrs *interfaces = NULL;
    struct ifaddrs *temp_addr = NULL;
    int success = 0;
    
    // retrieve the current interfaces - returns 0 on success
    success = getifaddrs(&interfaces);
    if (success == 0) {
        // Loop through linked list of interfaces
        temp_addr = interfaces;
        while (temp_addr != NULL) {
            if( temp_addr->ifa_addr->sa_family == AF_INET) {
                // Check if interface is en0 which is the wifi connection on the iPhone
                if ([[NSString stringWithUTF8String:temp_addr->ifa_name] isEqualToString:@"en0"]) {
                    // Get NSString from C String
                    address = [NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr)];
                }
            }
            
            temp_addr = temp_addr->ifa_next;
        }
    }
    // Free memory
    freeifaddrs(interfaces);
    
    return address;
}

#pragma mark -
#pragma mark GCDAsyncSocket Delegate

- (void)socket:(GCDAsyncSocket *)sock didAcceptNewSocket:(GCDAsyncSocket *)newSocket
{
    // This method is executed on the socketQueue (not the main thread)
    
    @synchronized(connectedSockets)
    {
        [connectedSockets addObject:newSocket];
    }
    
    NSString *host = [newSocket connectedHost];
    UInt16 port = [newSocket connectedPort];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        @autoreleasepool {
            
            [self log:FORMAT(@"Accepted client %@:%hu", host, port)];
            
        }
    });
    
    NSString *welcomeMsg = @"Welcome to the AsyncSocket Echo Server\r\n";
    NSData *welcomeData = [welcomeMsg dataUsingEncoding:NSUTF8StringEncoding];
    
    [newSocket writeData:welcomeData withTimeout:-1 tag:WELCOME_MSG];
    
    
    [newSocket readDataWithTimeout:READ_TIMEOUT tag:0];
    newSocket.delegate = self;
    
    //    [newSocket readDataToData:[GCDAsyncSocket CRLFData] withTimeout:READ_TIMEOUT tag:0];
}

- (void)socket:(GCDAsyncSocket *)sock didWriteDataWithTag:(long)tag
{
    // This method is executed on the socketQueue (not the main thread)
    NSLog(@"Data written");
    if (tag == ECHO_MSG)
    {
        [sock readDataToData:[GCDAsyncSocket CRLFData] withTimeout:100 tag:0];
        
    }
}

- (void)socket:(GCDAsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag
{
    dispatch_async(dispatch_get_main_queue(), ^{
    NSLog(@"== didReadData %@ ==", sock.description);
    
    NSString *msg = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    
    [self log:msg];
     [self displayText:[NSString stringWithFormat:@"Message received : %@ \n",msg]];
    self.synthesizer = [[AVSpeechSynthesizer alloc] init];
    AVSpeechUtterance *utterance = [[AVSpeechUtterance alloc] initWithString:msg];
    utterance.rate = 0.2;
    [self.synthesizer speakUtterance:utterance];
    [self perform:msg];
    [sock readDataWithTimeout:READ_TIMEOUT tag:0];
 });
 }

/**
 * This method is called if a read has timed out.
 * It allows us to optionally extend the timeout.
 * We use this method to issue a warning to the user prior to disconnecting them.
 **/
- (NSTimeInterval)socket:(GCDAsyncSocket *)sock shouldTimeoutReadWithTag:(long)tag
                 elapsed:(NSTimeInterval)elapsed
               bytesDone:(NSUInteger)length
{
    if (elapsed <= READ_TIMEOUT)
    {
        NSString *warningMsg = @"Are you still there?\r\n";
        NSData *warningData = [warningMsg dataUsingEncoding:NSUTF8StringEncoding];
        
        [sock writeData:warningData withTimeout:-1 tag:WARNING_MSG];
        
        return READ_TIMEOUT_EXTENSION;
    }
    
    return 0.0;
}

- (void)socketDidDisconnect:(GCDAsyncSocket *)sock withError:(NSError *)err
{
    if (sock != listenSocket)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            @autoreleasepool {
                [self log:FORMAT(@"Client Disconnected")];
            }
        });
        
        @synchronized(connectedSockets)
        {
            [connectedSockets removeObject:sock];
        }
    }
}

@end
