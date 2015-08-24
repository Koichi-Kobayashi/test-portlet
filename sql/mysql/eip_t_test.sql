CREATE TABLE `eip_t_test`
(
    `test_id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL,
    `test_name` varchar (99) COLLATE utf8_unicode_ci NOT NULL,
    `note` text COLLATE utf8_unicode_ci,
    `url` text COLLATE utf8_unicode_ci,
    `create_date` date DEFAULT NULL,
    `update_date` datetime DEFAULT NULL,
    PRIMARY KEY (`test_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
