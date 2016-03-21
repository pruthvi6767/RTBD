//
//  ViewController.h
//  RoboMeBasicSample
//
//  Copyright (c) 2013 WowWee Group Limited. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <RoboMe/RoboMe.h>
#import <SpeechKit/SpeechKit.h>

@interface ViewController : UIViewController <RoboMeDelegate, SpeechKitDelegate, SKRecognizerDelegate>

@property (strong, nonatomic) SKRecognizer* voiceSearch;


@property (weak, nonatomic) IBOutlet UITextField *KafkaInput;
@property (strong, nonatomic) IBOutlet UITextView *outputTextView;

@property (weak, nonatomic) IBOutlet UILabel *edgeLabel;
@property (weak, nonatomic) IBOutlet UILabel *chest20cmLabel;
@property (weak, nonatomic) IBOutlet UILabel *chest50cmLabel;
@property (weak, nonatomic) IBOutlet UILabel *cheat100cmLabel;
@property (strong, nonatomic) IBOutlet UIButton *recordButton;



@end
