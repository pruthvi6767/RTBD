//
//  ViewController.h
//  RoboMeBasicSample
//
//  Copyright (c) 2013 WowWee Group Limited. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <RoboMe/RoboMe.h>

@interface ViewController : UIViewController <RoboMeDelegate>

@property (weak, nonatomic) IBOutlet UITextView *outputTextView;
@property (weak, nonatomic) IBOutlet UILabel *textLabel;
@property (weak, nonatomic) IBOutlet UITextView *textTable;
@property (strong, nonatomic) IBOutlet UILabel *tweetViewer;
@property (strong, nonatomic) IBOutlet UILabel *htmlViewer;
@property (strong, nonatomic) IBOutlet UILabel *ratingViewer;

@property (weak, nonatomic) IBOutlet UILabel *edgeLabel;
@property (weak, nonatomic) IBOutlet UILabel *chest20cmLabel;
@property (weak, nonatomic) IBOutlet UILabel *chest50cmLabel;
@property (weak, nonatomic) IBOutlet UILabel *cheat100cmLabel;

@end
