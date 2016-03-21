//
//  ThirdViewController.m
//  Assignment1
//
//  Created by Vineeth Reddy Kanupuru on 9/5/15.
//  Copyright (c) 2015 Prudhvi. All rights reserved.
//

#import "ThirdViewController.h"
#import "ViewController.h"  
#import "SecondViewController.h"    

@interface ThirdViewController ()
@property (strong, nonatomic) IBOutlet UIImageView *showImage;

@end

@implementation ThirdViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/
- (IBAction)tappedOnImage:(id)sender {
    _showImage.image = [UIImage imageNamed:@"applebig.jpg"];
}
- (IBAction)tappedOnSecond:(id)sender {
    SecondViewController  *con =[self.storyboard instantiateViewControllerWithIdentifier:@"SecondViewController"];
    [self presentViewController:con animated:YES completion:NULL];
    
}
- (IBAction)tappedOnHome:(id)sender {
    ViewController *control =[self.storyboard instantiateViewControllerWithIdentifier:@"ViewController"];
    [self presentViewController:control animated:YES completion:NULL];
}

@end
