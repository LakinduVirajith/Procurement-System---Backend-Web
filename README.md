# Procurement for the Construction Industry - Web Backend

## 1. Introduction

Our project is dedicated to revolutionizing procurement processes within the construction industry, 
streamlining resource management, and ensuring efficiency and user-friendliness. We've harnessed the 
power of cutting-edge technologies, such as Spring Boot, Next.js, and React Native, to develop a 
comprehensive system tailored to the unique requirements of this field.

In this comprehensive report, we will embark on an extensive exploration of our project, covering crucial 
aspects such as class diagrams and entity relationship diagrams (ERDs). Class diagrams serve as visual 
blueprints for understanding the organization and interactions of our software components, while ERDs 
offer structured insights into our database and its relationships.

These diagrams act as essential guides, facilitating our comprehension of our project's architecture, data 
model definition, and the establishment of connections between diverse entities. We'll also delve into 
the implementation and code structure, diving into the authentication and security measures, user roles 
and permissions, and the array of features that empower our system.

Our integration with Swapper API will be discussed to highlight our project's connectivity with external 
systems. The report will further provide a reflective account of the lessons learned throughout the 
project's development.

In conclusion, this report offers a comprehensive understanding of our project's development, 
highlighting the role of class diagrams and ERDs, the implementation, authentication, and security 
measures, user roles, and permissions, the impressive feature set, and integration with external 
systems. By delving into these aspects, we aim to provide a holistic view of our project's journey and 
insights gained during its development

## 2. Class Diagram

<img src="diagrams/Class Diagram.png">

## 3. Entity Relationship Diagram (ERD)

<img src="diagrams/ER Diagram.png">

## 4. Implementation and Code Structure

In this section, we'll delve into the nuts and bolts of our project's implementation and code structure. 
It's here that we take a closer look at how we've transformed our ideas into a fully functional system.

#### ✓ Backend Code Structure:

Our system's backend, powered by Spring Boot, plays a pivotal role in delivering a robust and efficient 
experience. The codebase is organized into well-defined controllers, services, and entities. We'll explore 
how the "Item Controller" and "Order Controller" manage operations related to items and orders, 
offering a clear separation of concerns. These controllers provide endpoints for essential actions like 
adding, updating, and deleting items and orders. We'll also discuss how this architecture enhances 
manageability and scalability.

#### ✓ Frontend and Mobile Code Structure:

The front end, built with Next.js, offers a user-friendly interface for interaction with the system. 
Simultaneously, we've developed a mobile application using React Native, extending the accessibility of 
our system. These codebases are designed with a strong focus on user experience and seamless 
interaction.

#### ✓ Alignment with Class Diagram and ERD:

We've ensured that our code structure closely aligns with the concepts illustrated in our class diagram 
and entity relationship diagram (ERD). The class diagram's "Item" and "Order" classes are mirrored by 
our "Item" and "Order" entities, and the ERD's depiction of relationships between entities reflects our 
database structure accurately. We'll highlight how this alignment has helped us execute the project as 
visualized and maintained a clear link between architecture and implementation.

## 5. Authentication and Security

The security and authentication of our system are of paramount importance, ensuring the 
confidentiality and integrity of data. In this section, we'll explore the robust measures we've 
implemented to safeguard our system.

#### ✓ User Authentication:

A robust user authentication system that we've integrated lets users safely create accounts, log in, and 
view their profiles. This is especially important to make sure that data inside our system can only be 
accessed and altered by authorized personnel. We'll go over how user sessions are maintained for a 
seamless experience, as well as the procedures for user registration and authentication.

#### ✓ Role-Based Access Control:

To maintain a structured approach to access, our project incorporates a role-based access control 
system. Users are categorized into roles, such as site managers, procurement managers, suppliers, and 
administrators, each with a specific set of permissions. We'll examine how this system helps in 
controlling and managing access to various system features and functionalities.

#### ✓ Token-Based Authentication:

Token-based authentication has been integrated to validate the identity of users. We'll delve into how 
this mechanism generates tokens upon user login and validates them for each subsequent request, 
ensuring secure and authorized interactions with the system.

#### ✓ Lessons in Security:

Throughout the development process, we've encountered challenges and learned valuable lessons 
about system security. We'll share insights into the vulnerabilities we've addressed, and the lessons 
learned to strengthen our system's defenses.

## 6. Features, User Roles and Permissions

Our system is thoughtfully designed with a diverse set of features tailored to the needs of various user 
roles. We've categorized users into distinct roles, each granted specific permissions for an organized and 
secure experience. Here's how it all comes together:

#### ✓ Features:

Our system offers a rich array of features, which have been thoughtfully crafted to streamline 
procurement processes for construction sites. These features include:

1. Item Management: The site manager can add, update, and remove items from the system, 
ensuring that construction sites have access to the materials they require.

2. Order Management: The site manager can create, update, and manage orders, improving the 
efficiency and transparency of the procurement process.

3. User Management: Administrators can efficiently oversee and manage user accounts, allocate 
or deallocate users to specific sites, and maintain a comprehensive overview of all site users.

4. Supplier Interaction: Procurement managers can connect with suppliers, assign them to fulfill 
specific orders, and facilitate seamless procurement processes.

5. Site Information: The supplier can access crucial site details, and suppliers can confirm the 
delivery of orders.

#### ✓ User Roles and Permissions:

Our system categorizes users into specific roles, each aligned with their responsibilities and access 
permissions. Here's a detailed breakdown of these roles:

1. Site Manager: Site managers have the authority to manage the details of their respective 
construction sites, including adding, updating, and deleting site information. 

2. Procurement Manager: Procurement managers oversee supplier interactions, approve orders, 
and make key procurement decisions. They wield control over order management and supplier 
allocation.

3. Supplier: Suppliers can confirm order deliveries and interact with procurement managers to fulfill 
orders.

4. Administrator: Administrators have broad control over all aspects of the system, including user 
management and site management.

#### ✓ Permissions:

To ensure data security and operational integrity, each role is associated with specific permissions. Users 
can only perform actions that are relevant to their role, enhancing overall system security and efficiency.

1. Site Manager: Can access and manage APIs starting with "site-manager."

2. Administrator: Has access to all APIs starting with "super-admin."

3. Procurement Manager: Can utilize APIs starting with "procurement-manager."

4. Supplier: Is restricted to APIs starting with "supplier."

## 7. Integration with Swapper API

Swagger API is a popular open-source framework that helps developers design, build, document, and 
consume RESTful APIs. It provides a set of tools and resources that make it easy to create APIs that are 
both powerful and easy to use.

The foundation of Swagger API is Open API, a common format for describing RESTful APIs. Open API is a 
widely supported JSON-based format that is simple to read and write using a variety of tools and 
libraries.

When creating RESTful APIs in Spring Boot, Swagger API is a great option because of its many features, 
which include:

1. Automatic documentation generation: Your API's documentation, which includes thorough 
explanations of each endpoint, the parameters it takes, and the answers it returns, can be 
automatically generated by Swagger API.

2. API validation: In addition to creating test cases for your API, Swagger API can validate it against 
the Open API specification. This can assist you in finding and addressing API bugs prior to putting 
them into production.

## 8. Reflection and Lessons Learned

Our project journey has been a learning experience from start to finish. Here's a quick look at what 
we've discovered:

1. Tech Power: We've successfully used advanced technologies to build a responsive procurement 
system. This project has been a lesson in tech integration.

2. User-Focused: Making the users easier has been our mantra. Our system is intuitive and userfriendly.

3. Security Savvy: We've learned how to keep data secure and grant the right access to the right 
people with our role and permissions system.

On the learning side:

1. Team Harmony: Collaboration and communication are key. Our project's success is proof that 
teamwork makes the dream work.

2. Constant Metting: We've embraced the idea that perfection is an ongoing process. Regular 
feedback and updates have made our system better.

## 9. Conclusion

In this journey to develop our procurement system, we've delved into the world of innovation, 
technology, and collaboration. While this project doesn't interact with a real-world scenario, it's a 
testament to our ability to create practical solutions. Here's a snapshot:

1. Tech-Forward Thinking: We've used modern technologies to craft an efficient procurement 
system.

2. User-Centric Design: Our system is user-friendly and intuitive, ensuring ease of use for all 
stakeholders.

3. Role-Based Control: By implementing role-based user permissions, we've secured data and 
ensured that users have access to the features relevant to their roles.

As we conclude this project, we're excited about the possibilities. Although it's designed for a campus 
project, it exemplifies our capacity to innovate and improve processes. This project represents a 
steppingstone to future endeavors and the potential to create lasting positive impacts.
