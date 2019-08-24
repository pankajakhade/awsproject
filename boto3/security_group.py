#!/usr/bin/python
import sys
import boto3
from botocore.exceptions import ClientError
ec2 = boto3.client('ec2')
try:
    response = ec2.describe_security_groups(GroupIds=[sys.argv[1]])
    print(response)
except ClientError as e:
    print(e)
