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

@property (weak, nonatomic) IBOutlet UILabel *edgeLabel;
@property (weak, nonatomic) IBOutlet UILabel *chest20cmLabel;
@property (weak, nonatomic) IBOutlet UILabel *chest50cmLabel;
@property (weak, nonatomic) IBOutlet UILabel *cheat100cmLabel;
@property (weak, nonatomic) IBOutlet UILabel *temper;
@property (weak, nonatomic) IBOutlet UILabel *weadesc;
@property (weak, nonatomic) IBOutlet UILabel *county;
@property (weak, nonatomic) IBOutlet UIImageView *weatherImage;
@property (weak, nonatomic) IBOutlet UILabel *top10Label;
@property (weak, nonatomic) IBOutlet UILabel *top10Count;
@property (weak, nonatomic) IBOutlet UILabel *top30Label;
@property (weak, nonatomic) IBOutlet UILabel *top30Count;

@end
