## sns-dynamic-subscribers

This project provides a solution for implementing dynamuic subscribers to a SNS topic for autoscaling container based workloads. We have used Amazon Elastic Container Service(ECS) but the solution can applied for Amazon Elastic Kubernetes Service (EKS) too. It uses events generated by container tasks to create and delete SNS topic subscriptions. 


## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This library is licensed under the MIT-0 License. See the LICENSE file.