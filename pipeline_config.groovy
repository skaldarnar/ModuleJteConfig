// restrict individual repository Jenkinsfiles
allow_scm_jenkinsfile = false

libraries{
  merge = false // disallow individual repos to contribute additional libraries
  moduleBuild
}
