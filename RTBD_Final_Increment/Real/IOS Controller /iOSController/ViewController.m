//
//  ViewController.m
//  iOSController
//
//
//  Copyright (c) 2015 umkc. All rights reserved.
//

#import "ViewController.h"
#import "GCDAsyncSocket.h"
#import <ifaddrs.h>
#import <arpa/inet.h>

#define WELCOME_MSG  0
#define ECHO_MSG     1
#define WARNING_MSG  2


#define READ_TIMEOUT 15.0
#define READ_TIMEOUT_EXTENSION 10.0

#define FORMAT(format, ...) [NSString stringWithFormat:(format), ##__VA_ARGS__]
#define PORT 3333


@interface ViewController () {
    dispatch_queue_t socketQueue;
    NSMutableArray *connectedSockets;
    BOOL isRunning;
    
    GCDAsyncSocket *listenSocket;
}
@property (strong, nonatomic) IBOutlet UITextField *ipAddress;
@property (strong, nonatomic) IBOutlet UITextField *portNumber;
@property (weak, nonatomic) IBOutlet UITextField *KafkaInput;
@property (strong, nonatomic) IBOutlet UITextView *outputTextView;

@property (nonatomic, strong) RoboMe *roboMe;

@end

// Create a Account in the following http://nuancemobiledeveloper.com/public/index.php?task=home for Application keys and other details

const unsigned char SpeechKitApplicationKey[] = {0x07, 0x91, 0x98, 0x86, 0xab, 0x57, 0xbf, 0x28, 0x71, 0x48, 0xb5, 0x73, 0xab, 0xed, 0x94, 0x48, 0x39, 0xcb, 0xe2, 0x76, 0xdf, 0xce, 0xeb, 0x58, 0x3c, 0x48, 0xca, 0xde, 0x2b, 0x95, 0x28, 0x32, 0x26, 0xc0, 0xb8, 0xd1, 0x18, 0x77, 0xfa, 0x89, 0xbf, 0xaf, 0xfc, 0x6b, 0x9c, 0x91, 0xb3, 0xd5, 0x5c, 0x95, 0x4c, 0x3b, 0x63, 0x37, 0x93, 0x9f, 0x7f, 0x5d, 0x7d, 0xaa, 0xf2, 0xa5, 0xdc, 0x18};



@implementation ViewController
@synthesize recordButton,voiceSearch;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
#pragma mark Socket Creation
    //Creation of Socket
    socket = [[GCDAsyncSocket alloc] initWithDelegate:self delegateQueue:dispatch_get_main_queue()];
    
    
    // Fill your ID and Host
    
    [SpeechKit setupWithID:@"NMDPTRIAL_mahidharvarma_gmail_com20150908153035"
                      host:@"sslsandbox.nmdp.nuancemobility.net"
                      port:    443
                    useSSL:YES
                  delegate:nil];
    SKEarcon* earconStart	= [SKEarcon earconWithName:@"earcon_listening.wav"];
    [SpeechKit setEarcon:earconStart forType:SKStartRecordingEarconType];
    
    // Do any additional setup after loading the view, typically from a nib.
    socketQueue = dispatch_queue_create("socketQueue", NULL);
    
    listenSocket = [[GCDAsyncSocket alloc] initWithDelegate:self delegateQueue:socketQueue];
    
    // Setup an array to store all accepted client connections
    connectedSockets = [[NSMutableArray alloc] initWithCapacity:1];
    
    isRunning = NO;
    
    NSLog(@"%@", [self getIPAddress]);
    
    [self toggleSocketState];   //Statrting the Socket
    self.KafkaInput.returnKeyType=UIReturnKeySearch;
    if (![UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        
        UIAlertView *myAlertView = [[UIAlertView alloc] initWithTitle:@"Error"
                                                              message:@"Device has no camera"
                                                             delegate:nil
                                                    cancelButtonTitle:@"OK"
                                                    otherButtonTitles: nil];
        
        [myAlertView show];
        
    }
    

    
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



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
    [socket disconnect];
}

#pragma mark -
#pragma mark UI functions
- (IBAction)tappedOnConnect:(id)sender {
    
#pragma mark Socket Connection
    NSLog(@"Tapped On Connect");
    if (([_ipAddress.text isEqual:NULL])||([_portNumber.text isEqual:NULL])) {
        NSLog(@"IPAddress or Port is Empty");
    }
    else
    {
        NSError *err = nil;
        if (![socket connectToHost:_ipAddress.text onPort:[_portNumber.text intValue] error:&err]) // Asynchronous!
        {
            // If there was an error, it's likely something like "already connected" or "no delegate set"
            NSLog(@"I goofed: %@", err);
        }
        NSLog(@"%@",_ipAddress.text);
        NSLog(@"%d",[_portNumber.text intValue]);
    }
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
    
    [newSocket readDataToData:[GCDAsyncSocket CRLFData] withTimeout:READ_TIMEOUT tag:0];
}


- (IBAction)tappedOnForward:(id)sender {
    [socket writeData:[@"FORWARD" dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:1];
}

- (IBAction)tappedOnRight:(id)sender {
    [socket writeData:[@"RIGHT" dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:1];
}
- (IBAction)tappedOnBackWard:(id)sender {
    [socket writeData:[@"BACKWARD" dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:1];
}
- (IBAction)tappedOnLeft:(id)sender {
    [socket writeData:[@"LEFT" dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:1];
}
- (IBAction)tappedOnRleft:(id)sender {
    [socket writeData:[@"ROTATE LEFT" dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:1];
}
- (IBAction)tappedOnRright:(id)sender {
    [socket writeData:[@"ROTATE RIGHT" dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:1];
}
- (IBAction)tappedOnHeadUp:(id)sender {
    [socket writeData:[@"HEAD UP" dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:1];
}
- (IBAction)tappedOnHeadDown:(id)sender {
    [socket writeData:[@"HEAD DOWN" dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:1];
}
- (IBAction)tappedOnWeather:(id)sender {
    if (_textFromVoice.text) {
        NSString *weath = [NSString stringWithFormat:@"TEMP %@",_textFromVoice.text];
        [socket writeData:[(@"%@",weath) dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:1];
        
    }
    
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
    
    NSLog(@"== didReadData %@ ==", sock.description);
    
    NSString *msg = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    
    [self log:msg];
    [self displayText:[NSString stringWithFormat:@"Message received : %@ \n",msg]];
    //[self perform:msg];
    [sock readDataWithTimeout:READ_TIMEOUT tag:0];
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
        
        // [sock writeData:warningData withTimeout:-1 tag:WARNING_MSG];
        
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



- (IBAction)tappedOnRecipe:(id)sender {
    if (_textFromVoice.text) {
        NSString *food = [NSString stringWithFormat:@"FOOD %@",_textFromVoice.text];
        
        [socket writeData:[(@"%@",food) dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:1];
    }
    

}

- (IBAction)tappedOnRecord:(id)sender {
    
    
    
    if (transactionState == TS_RECORDING) {
        [voiceSearch stopRecording];
    }
    else if (transactionState == TS_IDLE) {
        SKEndOfSpeechDetection detectionType;
        NSString* recoType;
        NSString* langType;
        
        transactionState = TS_INITIAL;
        
        //      alternativesDisplay.text = @"";
        
        /* 'Dictation' is selected */
        detectionType = SKLongEndOfSpeechDetection; /* Dictations tend to be long utterances that may include short pauses. */
        recoType = SKDictationRecognizerType; /* Optimize recognition performance for dictation or message text. */
        langType = @"en_US";
        /* Nuance can also create a custom recognition type optimized for your application if neither search nor dictation are appropriate. */
        
        NSLog(@"Recognizing type:'%@' Language Code: '%@' using end-of-speech detection:%lu.", recoType, langType, (unsigned long)detectionType);
        
        // if (voiceSearch) [voiceSearch release];
        
        voiceSearch = [[SKRecognizer alloc] initWithType:recoType
                                               detection:detectionType
                                                language:langType
                                                delegate:self];

}
}
- (IBAction)tappedOnSendCommand:(id)sender {
    [socket writeData:[_textFromVoice.text dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:1];

}

# pragma mark -
# pragma mark GCDAsynSocket delegate methods
- (void)socket:(GCDAsyncSocket *)sender didConnectToHost:(NSString *)host port:(UInt16)port
{
    NSLog(@"Cool, I'm connected! That was easy.");
}



# pragma mark -
# pragma mark Voice Recogniser

- (void)recognizer:(SKRecognizer *)recognizer didFinishWithResults:(SKRecognition *)results
{
    NSLog(@"Got results.");
    NSLog(@"Session id [%@].", [SpeechKit sessionID]); // for debugging purpose: printing out the speechkit session id
    
    long numOfResults = [results.results count];
    
    transactionState = TS_IDLE;
    [recordButton setTitle:@"Record" forState:UIControlStateNormal];
    //
    if (numOfResults > 0)
    {
        //Result to Text Box
        [_textFromVoice setText:[results firstResult]];
            }
    if (numOfResults > 1)
    {
        //        alternativesDisplay.text = [[results.results subarrayWithRange:NSMakeRange(1, numOfResults-1)] componentsJoinedByString:@"\n"];
    }
    if (results.suggestion) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Suggestion"
                                                        message:results.suggestion
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
        // [alert release];
        
    }
    
    //  [voiceSearch release];
    // voiceSearch = nil;
}
- (void)recognizer:(SKRecognizer *)recognizer didFinishWithError:(NSError *)error suggestion:(NSString *)suggestion
{
    NSLog(@"Got error.");
    NSLog(@"Session id [%@].", [SpeechKit sessionID]); // for debugging purpose: printing out the speechkit session id
    
    transactionState = TS_IDLE;
    [recordButton setTitle:@"Record" forState:UIControlStateNormal];
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error"
                                                    message:[error localizedDescription]
                                                   delegate:nil
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
    // [alert release];
    
    if (suggestion) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Suggestion"
                                                        message:suggestion
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
        //  [alert release];
        
    }
    
    //  [voiceSearch release];
    voiceSearch = nil;
}
- (void)recognizerDidBeginRecording:(SKRecognizer *)recognizer
{
    NSLog(@"Recording started.");
    
    transactionState = TS_RECORDING;
    [recordButton setTitle:@"Recording..." forState:UIControlStateNormal];
    // [self performSelector:@selector(updateVUMeter) withObject:nil afterDelay:0.05];
}

- (void)recognizerDidFinishRecording:(SKRecognizer *)recognizer
{
    NSLog(@"Recording finished.");
    
    //    [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(updateVUMeter) object:nil];
    //
    transactionState = TS_PROCESSING;
    [recordButton setTitle:@"Processing..." forState:UIControlStateNormal];
}
- (IBAction)takePic:(id)sender {
    UIImagePickerController *picker = [[UIImagePickerController alloc] init];
    picker.delegate = self;
    picker.allowsEditing = YES;
    picker.sourceType = UIImagePickerControllerSourceTypeCamera;
    
    [self presentViewController:picker animated:YES completion:NULL];
}
- (IBAction)selectPic:(id)sender {
    UIImagePickerController *picker = [[UIImagePickerController alloc] init];
    picker.delegate = self;
    picker.allowsEditing = YES;
    picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    
    [self presentViewController:picker animated:YES completion:NULL];
}
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    
    chosenImage = info[UIImagePickerControllerEditedImage];
   // self.ImageView.image = chosenImage;
    
    [picker dismissViewControllerAnimated:YES completion:NULL];
    
}
- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    
    [picker dismissViewControllerAnimated:YES completion:NULL];
    
}

- (IBAction)sendPic:(id)sender {
    if (chosenImage) {
        NSLog(@"Recieved Image");
    }
    
    if (isRunning) {
        
        GCDAsyncSocket *newSocket = [connectedSockets lastObject];
        
        
        
        
        NSData *data = UIImageJPEGRepresentation(chosenImage, 0.8);
        NSString *base64 = [data base64Encoding];
        
        [newSocket writeData:[base64 dataUsingEncoding:NSUTF8StringEncoding] withTimeout:-1 tag:(1)];
        [newSocket writeData:[GCDAsyncSocket CRLFData] withTimeout:-1 tag:1];
        
        
        
        
    }

}

@end
