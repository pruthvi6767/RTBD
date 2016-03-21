//
//  ViewController.m
//  Assignment1
//
//  Created by Vineeth Reddy Kanupuru on 9/5/15.
//  Copyright (c) 2015 Prudhvi. All rights reserved.
//

#import "ViewController.h"
#import "SecondViewController.h"
@interface ViewController ()

@property (strong, nonatomic) IBOutlet UILabel *showName;
@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (IBAction)tappedOnShow:(id)sender {
    _showName.text=@"Prudhvi";
}
- (IBAction)tappedOnNext:(id)sender {
    SecondViewController *controller = [self.storyboard instantiateViewControllerWithIdentifier:@"SecondViewController"];
    [self presentViewController:controller animated:YES completion:NULL];
}

@end
