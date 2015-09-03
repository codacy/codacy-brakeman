User input in an eval statement is VERY dangerous, so this will always raise a warning.
Brakeman looks for calls to eval, instance_eval, class_eval, and module_eval.

[SOURCE](http://brakemanscanner.org/docs/warning_types/dangerous_evaluation/)