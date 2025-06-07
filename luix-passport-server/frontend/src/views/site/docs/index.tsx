import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { IconBook, IconCode, IconKey, IconArrowRight } from "@tabler/icons-react";

export default function DocsPage() {
  const docSections = [
    {
      title: "Installation",
      description: "Get started by installing our SDK and setting up your project.",
      link: "/docs/installation",
      icon: <IconCode className="h-5 w-5 text-primary mr-2" />,
    },
    {
      title: "Authentication",
      description: "Learn how to implement secure authentication flows.",
      link: "/docs/authentication",
      icon: <IconKey className="h-5 w-5 text-primary mr-2" />,
    },
    {
      title: "API Reference",
      description: "Explore our comprehensive API endpoints and usage.",
      link: "/docs/api-reference",
      icon: <IconBook className="h-5 w-5 text-primary mr-2" />,
    },
  ];

  return (
    <div className="container relative grid flex-col justify-center lg:max-w-none lg:grid-cols-3">
      {/* Left side - 1/3 width */}
      <div className="lg:flex relative hidden lg:col-span-1 xl:px-12 lg:px-2 ms-10 mb-36">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="h-14 my-12"
          />
          <h2 className="lg:text-4xl text-3xl font-extrabold lg:leading-[50px]">
            Documentation
          </h2>
          <h4 className="mt-6 text-lg">
            Everything you need to build with our platform.
          </h4>
          <div className="mt-10">
            <Button
              asChild
              variant="outline"
              className="px-8 py-4 h-14 text-base rounded-2xl"
            >
              <Link to="/sign-up">Get Started</Link>
            </Button>
          </div>
          <nav className="mt-10">
            <ul className="space-y-4">
              {docSections.map((section, index) => (
                <li key={index}>
                  <Link
                    to={section.link}
                    className="flex items-center text-base text-muted-foreground hover:text-primary"
                  >
                    {section.icon}
                    {section.title}
                  </Link>
                </li>
              ))}
            </ul>
          </nav>
        </div>
      </div>

      {/* Right side - 2/3 width */}
      <div className="flex flex-col items-center w-full h-full lg:col-span-2 xl:px-32 lg:px-10 px-5 rounded-3xl py-10 overflow-y-auto">
        <div className="flex-1">
          <h1 className="text-3xl font-bold mb-2">Developer Documentation</h1>
          <p className="text-lg text-muted-foreground mb-10">
            Comprehensive guides and references to help you integrate and build with our platform.
          </p>

          <div className="grid md:grid-cols-2 gap-6">
            {docSections.map((section, index) => (
              <div
                key={index}
                className="border rounded-2xl p-6 hover:shadow-md transition-shadow flex flex-col"
              >
                <div className="flex items-center mb-3">
                  {section.icon}
                  <h3 className="text-xl font-semibold">{section.title}</h3>
                </div>
                <p className="text-muted-foreground mb-6 grow">{section.description}</p>
                <Button
                  asChild
                  variant="outline"
                  className="w-full py-4 h-14 text-base rounded-2xl"
                >
                  <Link to={section.link} className="flex items-center justify-center">
                    Learn More
                    <IconArrowRight className="h-5 w-5 ml-2" />
                  </Link>
                </Button>
              </div>
            ))}
          </div>

          <div className="mt-16 border rounded-2xl p-6 bg-muted/50">
            <h3 className="text-xl font-semibold mb-4">Need Help?</h3>
            <div className="space-y-6">
              <div>
                <h4 className="font-medium">Community Support</h4>
                <p className="text-muted-foreground mt-1">
                  Join our developer community on Discord or GitHub to ask questions and share ideas.
                </p>
              </div>
              <div>
                <h4 className="font-medium">Enterprise Support</h4>
                <p className="text-muted-foreground mt-1">
                  Get dedicated support with our Enterprise plan, including a personal account manager.
                </p>
              </div>
              <div>
                <h4 className="font-medium">API Status</h4>
                <p className="text-muted-foreground mt-1">
                  Check the status of our services at any time on our status page.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}