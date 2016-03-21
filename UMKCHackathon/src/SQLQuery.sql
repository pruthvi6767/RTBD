
-- phpMyAdmin SQL Dump
-- version 2.11.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 09, 2015 at 06:10 AM
-- Server version: 5.1.57
-- PHP Version: 5.2.17

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `a6015559_lr`
--

-- --------------------------------------------------------

--
-- Table structure for table `degree`
--

CREATE TABLE `degree` (
  `Degree_Id` int(11) NOT NULL AUTO_INCREMENT,
  `Degree_Name` varchar(255) NOT NULL,
  PRIMARY KEY (`Degree_Id`),
  UNIQUE KEY `Degree_Name` (`Degree_Name`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `degree`
--

INSERT INTO `degree` VALUES(2, 'Graduate');
INSERT INTO `degree` VALUES(3, 'PostGraduate');
INSERT INTO `degree` VALUES(1, 'Undergraduate');

-- --------------------------------------------------------

--
-- Table structure for table `hobby`
--

CREATE TABLE `hobby` (
  `Hobby_Id` int(11) NOT NULL AUTO_INCREMENT,
  `Hobby_Name` varchar(255) NOT NULL,
  PRIMARY KEY (`Hobby_Id`),
  UNIQUE KEY `Hobby_Name` (`Hobby_Name`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Dumping data for table `hobby`
--

INSERT INTO `hobby` VALUES(4, 'Badminton');
INSERT INTO `hobby` VALUES(2, 'Cricket');
INSERT INTO `hobby` VALUES(5, 'Football');
INSERT INTO `hobby` VALUES(1, 'Soccer');
INSERT INTO `hobby` VALUES(3, 'Swimming');
INSERT INTO `hobby` VALUES(6, 'Squash');
INSERT INTO `hobby` VALUES(7, 'Cooking');
INSERT INTO `hobby` VALUES(8, 'Table Tennis');
INSERT INTO `hobby` VALUES(9, 'Rugby');

-- --------------------------------------------------------

--
-- Table structure for table `nationality`
--

CREATE TABLE `nationality` (
  `Nationality_id` int(11) NOT NULL AUTO_INCREMENT,
  `Nationality_Name` varchar(32) NOT NULL,
  PRIMARY KEY (`Nationality_id`),
  UNIQUE KEY `Nationality_id` (`Nationality_id`),
  UNIQUE KEY `Nationality_Name` (`Nationality_Name`),
  KEY `Nationality_id_2` (`Nationality_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=20 ;

--
-- Dumping data for table `nationality`
--

INSERT INTO `nationality` VALUES(12, 'Indian');
INSERT INTO `nationality` VALUES(13, 'Nigerian');
INSERT INTO `nationality` VALUES(14, 'Jamaican');
INSERT INTO `nationality` VALUES(15, 'American');
INSERT INTO `nationality` VALUES(16, 'Chinese');
INSERT INTO `nationality` VALUES(17, 'British');
INSERT INTO `nationality` VALUES(18, 'Brazilian');
INSERT INTO `nationality` VALUES(19, 'Canadian');

-- --------------------------------------------------------

--
-- Table structure for table `persons`
--

CREATE TABLE `persons` (
  `User_Id` int(255) NOT NULL,
  `DOB` date NOT NULL,
  `Sex` enum('M','F') NOT NULL,
  `Mobile_No` int(11) NOT NULL,
  `loc_ltd` double DEFAULT NULL,
  `loc_long` double DEFAULT NULL,
  `city` varchar(200) DEFAULT NULL,
  `state` varchar(200) DEFAULT NULL,
  `country` varchar(200) DEFAULT NULL,
  `univ_name` varchar(200) DEFAULT NULL,
  `degree` varchar(300) NOT NULL,
  `specialization` varchar(300) NOT NULL,
  `nationality` varchar(300) NOT NULL,
  `hobbies` varchar(1000) NOT NULL,
  `street` varchar(1000) NOT NULL,
  `zip` varchar(10) NOT NULL,
  PRIMARY KEY (`User_Id`),
  KEY `Mobile_No` (`Mobile_No`),
  KEY `User_Id` (`User_Id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `persons`
--

INSERT INTO `persons` VALUES(24, '2015-04-15', 'M', 2147483647, 39.0362552, -94.5753394, 'Kansas City', 'MO', 'USA', 'University of Missouri Kansas City', 'Graduate', 'Electrical Engineering', 'Indian', 'Computer games', '', '64110');
INSERT INTO `persons` VALUES(8, '1992-11-21', 'M', 2147483647, 39.0362552, -94.5753394, 'Kansas City', 'MO', 'USA', 'University of Missouri Kansas City', 'Undergraduate', 'Computer Science', 'Indian', 'Soccer,Computer Games', '', '64110');
INSERT INTO `persons` VALUES(10, '1993-01-11', 'M', 2147483647, 39.0379444, -94.5985613, 'Kansas City', 'MO', 'USA', 'University of Missouri Kansas City', 'Post Graduate', 'Electrical Engineering', 'Chinese', 'soccer', '', '64112');
INSERT INTO `persons` VALUES(29, '2015-05-26', 'M', 2147483647, 17.4801969, 78.4171028000001, 'Hyderabad', 'Telangana', 'India', 'University of Texas at Arlington', 'Graduate', 'Computer Science', 'Indian', 'Testing', '', '500072');
INSERT INTO `persons` VALUES(17, '1993-01-11', 'M', 1234567890, 37.340162, -121.88, 'San Jose', 'CA', 'USA', 'San Jose State University', 'Undergraduate', 'Physics', 'Indian', 'Swimming', '', '95112');
INSERT INTO `persons` VALUES(13, '1993-03-09', 'M', 2147483647, 39.110891, -94.585508, 'Kansas City', 'MO ', 'USA', 'University of Missouri Kansas City', 'Graduate', 'Civil Engineering', 'British', 'Squash', '', '64105');
INSERT INTO `persons` VALUES(11, '1992-04-06', 'M', 2147483647, 39.134682, -94.515955, 'Kansas City', 'MO', 'USA', 'University of Missouri Kansas City', 'Undergraduate', 'Computer Science', 'Indian', 'Cooking', '', '64120');
INSERT INTO `persons` VALUES(20, '1955-05-19', 'M', 2147483647, 38.760071, -94.165297, 'Strassburg', 'MO', 'USA', 'Kansas State University', 'Undergraduate', 'Electrical Engineering', 'Chinese', 'Table Tennis', '', '64080');
INSERT INTO `persons` VALUES(19, '2014-05-07', 'F', 21421421, 32.7229925, -97.1520926, 'Arlington', 'TX', 'USA', 'University of Texas at Arlington', 'Graduate', 'Physics', 'Chinese', 'Reading,swimming', '', '76013');
INSERT INTO `persons` VALUES(25, '2003-08-23', 'F', 43151351, 39.4531611280739, -104.767684936523, 'Parker', 'CO', 'USA', 'University of Missouri Columbia', 'Graduate', 'Mechanical Engineering', 'American', 'Reading', 'Cherry Creek Trail', '80134');
INSERT INTO `persons` VALUES(33, '2002-07-02', 'M', 2147483647, 39.0362552, -94.5753394, 'Kansas City', 'MO', 'USA', 'University of Missouri Kansas City', 'Graduate', 'Electrical Engineering', 'American', 'soccer,swimming', '', '64110');
INSERT INTO `persons` VALUES(32, '1993-11-11', 'M', 2147483647, 51.5209966, -0.127844200000027, '', '', '', 'University of London', 'Graduate', 'Law', 'British', 'Cricket', '', '-1');

-- --------------------------------------------------------

--
-- Table structure for table `pm`
--

CREATE TABLE `pm` (
  `id` bigint(20) NOT NULL,
  `id2` int(11) NOT NULL,
  `title` varchar(256) NOT NULL,
  `user1` bigint(20) NOT NULL,
  `user2` bigint(20) NOT NULL,
  `message` text NOT NULL,
  `timestamp` int(10) NOT NULL,
  `user1read` varchar(3) NOT NULL,
  `user2read` varchar(3) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pm`
--


-- --------------------------------------------------------

--
-- Table structure for table `specialization`
--

CREATE TABLE `specialization` (
  `S_Id` int(11) NOT NULL AUTO_INCREMENT,
  `S_Name` varchar(255) NOT NULL,
  PRIMARY KEY (`S_Id`),
  UNIQUE KEY `S_Name` (`S_Name`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=115 ;

--
-- Dumping data for table `specialization`
--

INSERT INTO `specialization` VALUES(105, 'Bio Engineering');
INSERT INTO `specialization` VALUES(103, 'Civil Engineering');
INSERT INTO `specialization` VALUES(101, 'Computer Science');
INSERT INTO `specialization` VALUES(102, 'Electrical Engineering');
INSERT INTO `specialization` VALUES(104, 'Mechanical Engineering');
INSERT INTO `specialization` VALUES(108, 'Computer Engineering');
INSERT INTO `specialization` VALUES(109, 'Physics');
INSERT INTO `specialization` VALUES(110, 'Music');
INSERT INTO `specialization` VALUES(111, 'Statistics');
INSERT INTO `specialization` VALUES(112, 'Media');
INSERT INTO `specialization` VALUES(113, 'Maths');
INSERT INTO `specialization` VALUES(114, 'Law');

-- --------------------------------------------------------

--
-- Table structure for table `university`
--

CREATE TABLE `university` (
  `U_Id` int(11) NOT NULL AUTO_INCREMENT,
  `U_Name` varchar(255) NOT NULL,
  PRIMARY KEY (`U_Id`),
  UNIQUE KEY `U_Name` (`U_Name`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=37 ;

--
-- Dumping data for table `university`
--

INSERT INTO `university` VALUES(1, 'University of Missouri Kansas City');
INSERT INTO `university` VALUES(2, 'University of Texas at Arlington');
INSERT INTO `university` VALUES(3, 'University of South Florida');
INSERT INTO `university` VALUES(4, 'University of California Irvine');
INSERT INTO `university` VALUES(5, 'SUNY Buffalo');
INSERT INTO `university` VALUES(28, 'Kansas State University');
INSERT INTO `university` VALUES(29, 'University of Central Missouri');
INSERT INTO `university` VALUES(30, 'San Jose State University');
INSERT INTO `university` VALUES(31, 'Universirty of Illinois ');
INSERT INTO `university` VALUES(32, 'University of Missouri Columbia');
INSERT INTO `university` VALUES(33, 'University of Oxford');
INSERT INTO `university` VALUES(34, 'California State University Sacramento');
INSERT INTO `university` VALUES(35, 'Arizona State University');
INSERT INTO `university` VALUES(36, 'University of London');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL,
  `password` varchar(60) NOT NULL,
  `first_name` varchar(32) NOT NULL,
  `last_name` varchar(32) NOT NULL,
  `email` varchar(1024) NOT NULL,
  `email_code` varchar(32) NOT NULL,
  `country` varchar(50) NOT NULL,
  `active` int(11) DEFAULT '0',
  `password_recover` int(11) NOT NULL DEFAULT '0',
  `type` int(1) NOT NULL DEFAULT '0',
  `allow_email` int(11) NOT NULL DEFAULT '1',
  `profile` varchar(55) NOT NULL,
  `init` tinyint(1) NOT NULL DEFAULT '0',
  `p1` int(11) NOT NULL DEFAULT '0',
  `p2` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=34 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` VALUES(24, 'vineeth', '26f317e07d72dcc83f8650fc3bebc5334cd1fe58', 'Vineeth', 'Reddy', 'vreddy@gmail.com', 'eaa424ba55f90263e557aa928cacdcdb', '', 1, 0, 0, 1, '', 1, 1, 1);
INSERT INTO `users` VALUES(8, 'maxy68', '1dd1411617e768467912d0a1c326a25304faed3c', 'Prudhvi', 'Myneni', 'prudhvi.myneni@gmail.com', 'd1177a8ab38c4e1f0edda134efc6a608', '', 1, 0, 0, 0, 'images/profile/4d1d26a143.jpg', 1, 0, 1);
INSERT INTO `users` VALUES(13, 'Jon', '9c9d53ccd9db2b36bfb5c46aaeae8a8aa725523c', 'Jon', 'Snow', 'castleblackcommander@gmail.com', '656315889f50e9f901805906920d170c', '', 1, 1, 0, 1, '', 1, 0, 0);
INSERT INTO `users` VALUES(10, 'ViRu$m@N', '6a2f3a65a3a1d423e34aa6e8aaf78433e2377947', 'Mahidhar Varma', 'Dhanthuluri', 'whereismyfriend@outlook.com', '8b82c55866945c48d493d9f05332c48d', '', 1, 0, 0, 0, 'images/profile/36cc267cc4.jpg', 1, 0, 0);
INSERT INTO `users` VALUES(11, 'sg4t3', '62ca762a28dfbd318dc3472d242132d8ea7fd0dd', 'sudheer', 'go', 'gourishetty.sudheer4@gmail.com', '40c791c3c2dcc7234290959d9a1c5c1a', '', 1, 0, 0, 0, '', 1, 0, 0);
INSERT INTO `users` VALUES(29, 'rshaik', '3922ccbb40f2c9cb7cb6810ef9442c226c2ac3f4', 'Razikh', 'Shaik', 'razikhshaik@gmail.com', '28e50cbdd7e309484eebb8a6dcc75c43', '', 1, 0, 0, 1, '', 1, 0, 0);
INSERT INTO `users` VALUES(16, 'avi', '62ca762a28dfbd318dc3472d242132d8ea7fd0dd', 'avinash', 'j', 'avij@gmail.com', 'b1121ef5b1f74e4d665a7840ca672062', '', 1, 0, 0, 1, '', 0, 0, 0);
INSERT INTO `users` VALUES(15, 'dj', '290904fb9b0007212b1d5500ab6ef65cf3a0eb4e', 'akcent', 'dj', 'akcentdj@gmail.com', '2858983f3e41fb554f594e096de4813e', '', 1, 0, 0, 1, '', 1, 0, 0);
INSERT INTO `users` VALUES(17, 'cisco', '62ca762a28dfbd318dc3472d242132d8ea7fd0dd', 'Carl', 'Johnson', 'carl@gmail.com', 'f264d86701f08ca3fccd9866b08922c9', '', 1, 0, 0, 0, 'images/profile/7a1ec206d5.jpg', 1, 1, 1);
INSERT INTO `users` VALUES(20, 'silentstorm', 'c440517dd486de6b0f6baf412e957d4c225f579e', 'Bruce', 'Willis', 'mahidharvarma@gmail.com', 'c5d8c38e9fe23cbf58653b78ecc9dee2', '', 1, 1, 0, 0, '', 0, 1, 1);
INSERT INTO `users` VALUES(19, 'iris', '474fda8b3bb192cc778d8ab5fb0b80e80588b546', 'iris', 'west', 'iriswest@mail.com', 'e9cb45acbdf5c86669a2220552740f2d', '', 1, 0, 0, 1, 'images/profile/b28c2c7f43.jpg', 1, 0, 0);
INSERT INTO `users` VALUES(21, 'oliver', '10ab78d038b20aa45dec2daa61cca2d7757c6123', 'Oliver', 'Queen', 'oqueen@mail.com', '322ba69eb788a7ad133ddf4480d20a2f', '', 1, 0, 0, 1, '', 0, 0, 0);
INSERT INTO `users` VALUES(22, 'vivek', '62ca762a28dfbd318dc3472d242132d8ea7fd0dd', 'vivek', 'm', 'vivek.munukuntla@gmail.com', 'bd31fa7135ff7307731c57be97875c22', '', 1, 0, 0, 1, '', 0, 0, 0);
INSERT INTO `users` VALUES(23, 'avinash', '62ca762a28dfbd318dc3472d242132d8ea7fd0dd', 'avinash', 'j', 'chintujmlg@gmail.com', '014d3d4f97a6027d44e8927c2063ff84', '', 1, 0, 0, 1, '', 0, 0, 0);
INSERT INTO `users` VALUES(25, 'Khaleesi', '26f317e07d72dcc83f8650fc3bebc5334cd1fe58', 'Daenerys', 'Targaryen', 'khaleesi.92@gmail.com', 'bc8279436db36f9bcc520b384a601fc7', '', 1, 0, 0, 0, 'images/profile/b1f686b5fd.jpg', 1, 1, 1);
INSERT INTO `users` VALUES(33, 'ned92', '26f317e07d72dcc83f8650fc3bebc5334cd1fe58', 'Ned', 'Stark', 'myneni.prudhvi@gmail.com', '0ed6dad8b81911dc06211962d5d36ebc', '', 1, 0, 0, 0, 'images/profile/22f0aff828.jpg', 1, 1, 1);
INSERT INTO `users` VALUES(30, 'vinni329', 'db99793bbb73e76add02de861c48e2cd20abe04d', 'vineeth', 'reddy', 'vineeth329@gmail.com', '6e629a10f3bac8b7701d6e9208955564', '', 1, 0, 0, 1, '', 0, 0, 0);
INSERT INTO `users` VALUES(32, 'blazingphoenix', '26f317e07d72dcc83f8650fc3bebc5334cd1fe58', 'Peter', 'Parker', 'mahidhar_varma@yahoo.com', 'a541bcb39c6640040ddc3a75e9599d6b', '', 1, 0, 0, 0, 'images/profile/7bcae2a3a1.jpg', 1, 0, 0);
