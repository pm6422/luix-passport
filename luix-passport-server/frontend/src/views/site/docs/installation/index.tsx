import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { IconBook, IconCode, IconKey, IconTerminal2 } from "@tabler/icons-react";

export default function InstallationPage() {
  const docSections = [
    {
      title: "Installation",
      description: "Set up the SDK in your project.",
      link: "/docs/installation",
      icon: <IconCode className="h-5 w-5 text-primary mr-2" />,
    },
    {
      title: "Authentication",
      description: "Implement secure authentication flows.",
      link: "/docs/authentication",
      icon: <IconKey className="h-5 w-5 text-primary mr-2" />,
    },
    {
      title: "API Reference",
      description: "Explore our API endpoints.",
      link: "/docs/api-reference",
      icon: <IconBook className="h-5 w-5 text-primary mr-2" />,
    },
  ];

  return (
    <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-3">
      {/* Left side - 1/3 width */}
      <div className="lg:flex relative hidden lg:col-span-1 lg:px-12 ms-10 mb-36">
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
                    className={`flex items-center text-base ${
                      section.title === "Installation"
                        ? "text-primary font-semibold"
                        : "text-muted-foreground hover:text-primary"
                    }`}
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
      <div className="flex flex-col w-full h-full lg:col-span-2 xl:px-32 lg:px-20 px-5 rounded-3xl py-10 overflow-y-auto">
        <div className="flex-1">
          <h1 className="text-3xl font-bold mb-2">Installation</h1>
          <p className="text-lg text-muted-foreground mb-10">
            Get started by installing our SDK or CLI in your project.
          </p>

          <div className="space-y-12">
            {/* Node.js Installation */}
            <div className="border rounded-2xl p-6 hover:shadow-md transition-shadow">
              <h3 className="text-xl font-semibold mb-3 flex items-center">
                <IconCode className="h-5 w-5 text-primary mr-2" />
                Node.js
              </h3>
              <p className="text-muted-foreground mb-4">
                Install the SDK using npm or yarn.
              </p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
                  npm install @luix-universe/sdk
                </pre>
                <pre className="text-sm text-muted-foreground mt-2">
                  yarn add @luix-universe/sdk
                </pre>
              </div>
              <p className="text-muted-foreground mt-4">
                Initialize the SDK in your project:
              </p>
              <div className="bg-muted/50 p-4 rounded-lg mt-2">
                <pre className="text-sm text-muted-foreground">
{`import Luix from '@luix-universe/sdk';

const luix = new Luix({
  apiKey: 'your-api-key',
});
`}
                </pre>
              </div>
            </div>

            {/* Python Installation */}
            <div className="border rounded-2xl p-6 hover:shadow-md transition-shadow">
              <h3 className="text-xl font-semibold mb-3 flex items-center">
                <IconCode className="h-5 w-5 text-primary mr-2" />
                Python
              </h3>
              <p className="text-muted-foreground mb-4">
                Install the SDK using pip.
              </p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
                  pip install luix-universe
                </pre>
              </div>
              <p className="text-muted-foreground mt-4">
                Initialize the SDK in your project:
              </p>
              <div className="bg-muted/50 p-4 rounded-lg mt-2">
                <pre className="text-sm text-muted-foreground">
{`from luix_universe import Luix

luix = Luix(api_key='your-api-key')
`}
                </pre>
              </div>
            </div>

            {/* CLI Installation */}
            <div className="border rounded-2xl p-6 hover:shadow-md transition-shadow">
              <h3 className="text-xl font-semibold mb-3 flex items-center">
                <IconTerminal2 className="h-5 w-5 text-primary mr-2" />
                CLI
              </h3>
              <p className="text-muted-foreground mb-4">
                Install the CLI tool for managing your projects.
              </p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
                  npm install -g @luix-universe/cli
                </pre>
              </div>
              <p className="text-muted-foreground mt-4">
                Verify installation:
              </p>
              <div className="bg-muted/50 p-4 rounded-lg mt-2">
                <pre className="text-sm text-muted-foreground">
                  luix --version
                </pre>
              </div>
            </div>
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
                <h4 className="font-medium">Troubleshooting</h4>
                <p className="text-muted-foreground mt-1">
                  Check our troubleshooting guide for common installation issues.
                </p>
                <Button
                  asChild
                  variant="outline"
                  className="mt-2 px-4 py-2 h-10 text-sm rounded-2xl"
                >
                  <Link to="/docs/troubleshooting">View Guide</Link>
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}