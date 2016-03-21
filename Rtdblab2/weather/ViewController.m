//
//  ViewController.m
//  weather
//
//  Created by Vineeth Reddy Kanupuru on 9/8/15.
//  Copyright (c) 2015 Prudhvi. All rights reserved.
//

#import "ViewController.h"
#import "AFNetworking.h"    
#import "UIImageView+AFNetworking.h"
#import "AVFoundation/AVFoundation.h"

@interface ViewController ()
@property (strong, nonatomic) IBOutlet UITextField *searchfield;
@property (strong, nonatomic) IBOutlet UILabel *weadesc;
@property (strong, nonatomic) IBOutlet UILabel *county;
@property (strong, nonatomic) IBOutlet UIImageView *weatherImage;
@property (strong, nonatomic) AVSpeechSynthesizer *synthesizer;
@property (weak, nonatomic) IBOutlet UILabel *temper;

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
- (IBAction)tappedOnSearch:(id)sender {
    [_searchfield resignFirstResponder];
    NSString *BASEURL = @"http://api.openweathermap.org/data/2.5/weather?zip=";
    NSString *zip = @"london";
        NSString *urlrest=@",us";
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    if (_searchfield.text.length != 0) {
        zip=_searchfield.text;
    }
     NSString *url = [NSString stringWithFormat:@"%@%@%@", BASEURL, zip, urlrest];
    NSLog(@"%@", url);
    
    [manager GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSLog(@"JSON: %@", responseObject);
        NSDictionary   *total =responseObject   ;
        NSArray *array=[responseObject objectForKey:@"weather"];
        NSDictionary *dict=[array objectAtIndex:0];
        _weadesc.text=[dict objectForKey:@"description"];
        NSString *Cname= [ total objectForKey:@"name"];
        NSString *CountyName=[NSString stringWithFormat:@"City: %@",Cname];
        _county.text=CountyName;
        NSString  *imageurl=@"http://openweathermap.org/img/w/";
        NSString *filename=@"10d";
        NSString *ext=@".png";
        filename   =[dict objectForKey:@"icon"];
        NSString  *urlnew= [NSString stringWithFormat:@"%@%@%@", imageurl,filename,ext];
       
        NSDictionary *main =[total objectForKey:@"main"];
        NSLog(@"%@",[main objectForKey:@"temp"]);
        NSString *temperature = [main objectForKey:@"temp"];
        NSInteger myint = [temperature integerValue];
        myint=myint-273;
    
        _temper.text=[NSString stringWithFormat:@"Temperature: %ld",(long)myint];
         NSString *Descript=[NSString stringWithFormat:@"The Temperature at %@ is %ld Celcius and the weather looks like %@",Cname,(long)myint,[dict objectForKey:@"description" ]];
        [_weatherImage setImageWithURL:([NSURL URLWithString:urlnew]) ];
        self.synthesizer = [[AVSpeechSynthesizer alloc] init];
        AVSpeechUtterance *utterance = [[AVSpeechUtterance alloc] initWithString:Descript];
        utterance.rate = 0.2;
       [self.synthesizer speakUtterance:utterance];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error: %@", error);
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error"
                                                        message:[error description]
                                                       delegate:nil
                                              cancelButtonTitle:@"Ok"
                                              otherButtonTitles:nil, nil];
        [alert show];
    }];


}

@end
